package io.polaris.core.aop;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import io.polaris.core.asm.internal.Signature;
import io.polaris.core.asm.proxy.Interceptor;
import io.polaris.core.asm.proxy.Invocation;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.tuple.Tuple2;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class Aspect implements Interceptor {
	private static final Logger log = Loggers.of(Aspect.class);

	/** 目标对象 */
	private final Object target;
	/** 织入拦截器缓存 */
	private final Map<Method, Advisor> methodCache = new ConcurrentHashMap<>(32);
	/** 所有可织入的通知 */
	private final List<MatchedAdvice> matchedAdvices = new CopyOnWriteArrayList<>();
	/** 目标类所有方法 */
	private final Map<Method, MethodHandle> targetMethods;
	/** 可按方法名与参数类型匹配的方法 */
	private final Map<Signature, List<Tuple2<Method, MethodHandle>>> matchedMethods;

	/** 重写方法生成器 */
	private Function<Signature, Function<Object[], Object>> overrideGenerator;
	/** 重写方法映射表 */
	private Map<Signature, Function<Object[], Object>> overrideMapper;
	/** 默认方法生成器 */
	protected Function<Signature, Function<Object[], Object>> defaultGenerator;
	/** 默认方法映射表 */
	protected Map<Signature, Function<Object[], Object>> defaultMapper;

	public Aspect(Object target, Advice... advices) {
		this(target, Arrays.asList(advices));
	}

	public Aspect(Object target, Iterable<Advice> advices) {
		this.target = target;
		for (Advice advice : advices) {
			addAdvice(advice);
		}
		Map<Method, MethodHandle> allMethods = new LinkedHashMap<>();
		getAllAccessibleMethods(target.getClass(), allMethods, MethodHandles.lookup());
		Map<Signature, List<Tuple2<Method, MethodHandle>>> matchedMethods = new HashMap<>();
		allMethods.forEach((method, handle) -> {
			Signature signature = Signature.of(method);
			List<Tuple2<Method, MethodHandle>> list = matchedMethods.computeIfAbsent(signature, k -> new ArrayList<>());
			list.add(Tuple2.of(method, handle));
		});
		matchedMethods.forEach((signature, list) -> {
			Map<Method, Integer> indexes = new HashMap<>();
			for (int i = 0; i < list.size(); i++) {
				Tuple2<Method, MethodHandle> tuple2 = list.get(i);
				indexes.put(tuple2.getFirst(), list.indexOf(tuple2));
			}
			// 按访问权限排序，优先使用public方法，否则保持加入顺序
			list.sort((o1, o2) -> {
				Method m1 = o1.getFirst();
				Method m2 = o2.getFirst();
				boolean p1 = Modifier.isPublic(m1.getModifiers());
				boolean p2 = Modifier.isPublic(m2.getModifiers());
				if (p1) {
					if (p2) {
						return Integer.compare(indexes.get(m1), indexes.get(m2));
					}
					return -1;
				} else if (p2) {
					return 1;
				} else {
					return Integer.compare(indexes.get(m1), indexes.get(m2));
				}
			});
		});
		this.targetMethods = allMethods;
		this.matchedMethods = matchedMethods;
	}

	public Object target() {
		return target;
	}

	public void addAdvice(Advice... advices) {
		this.matchedAdvices.add(new MatchedAdvice(null, advices));
		clearCache();
	}

	public void addAdvice(Predicate<Signature> predicate, Advice... advices) {
		this.matchedAdvices.add(new MatchedAdvice(predicate == null ? null : (s, n) -> predicate.test(s), advices));
		clearCache();
	}

	public void addAdvice(BiPredicate<Signature, Method> predicate, Advice... advices) {
		this.matchedAdvices.add(new MatchedAdvice(predicate, advices));
		clearCache();
	}

	public void override(Function<Signature, Function<Object[], Object>> overrideGenerator) {
		this.overrideGenerator = overrideGenerator;
		clearCache();
	}

	public void override(Signature method, Function<Object[], Object> overrideFunction) {
		if (this.overrideMapper == null) {
			this.overrideMapper = new ConcurrentHashMap<>();
		}
		this.overrideMapper.put(method, overrideFunction);
		clearCache();
	}

	public void override(Map<Signature, Function<Object[], Object>> overrideMapper) {
		if (overrideMapper == null || overrideMapper.isEmpty()) {
			this.overrideMapper = null;
		}
		this.overrideMapper = new ConcurrentHashMap<>(overrideMapper);
		clearCache();
	}

	public void defaults(Function<Signature, Function<Object[], Object>> defaultGenerator) {
		this.defaultGenerator = defaultGenerator;
		clearCache();
	}

	public void defaults(Signature method, Function<Object[], Object> defaultFunction) {
		if (this.defaultMapper == null) {
			this.defaultMapper = new ConcurrentHashMap<>();
		}
		this.defaultMapper.put(method, defaultFunction);
		clearCache();
	}

	public void defaults(Map<Signature, Function<Object[], Object>> defaultMapper) {
		if (defaultMapper == null || defaultMapper.isEmpty()) {
			this.defaultMapper = null;
		}
		this.defaultMapper = new ConcurrentHashMap<>(defaultMapper);
		clearCache();
	}

	public void clearCache() {
		methodCache.clear();
	}

	@Nonnull
	private Advisor generateAdvisor(Method method) {
		Signature signature = Signature.of(method);

		Function<Object[], Object> override = null;
		// 如存在重写实现，则使用
		if (this.overrideMapper != null) {
			override = this.overrideMapper.get(signature);
		}
		if (override == null && this.overrideGenerator != null) {
			override = this.overrideGenerator.apply(signature);
		}
		// 不存在重写实现，且目标类不存在此方法，则尝试使用默认实现
		if (override == null) {
			if (this.defaultMapper != null || this.defaultGenerator != null) {
				if (!this.matchedMethods.containsKey(signature)) {
					// 方法不存在，尝试使用默认方法
					Function<Object[], Object> defaultFunction = null;
					if (this.defaultMapper != null) {
						defaultFunction = this.defaultMapper.get(signature);
					}
					if (defaultFunction == null && this.defaultGenerator != null) {
						defaultFunction = this.defaultGenerator.apply(signature);
					}
					if (defaultFunction != null) {
						override = defaultFunction;
					}
				}
			}
		}

		// 获取匹配的切面通知，生成拦截器
		Set<Advice> advices = new LinkedHashSet<>();
		for (MatchedAdvice matchedAdvice : matchedAdvices) {
			if (matchedAdvice.accept(signature, method)) {
				advices.addAll(Arrays.asList(matchedAdvice.getAdvices()));
			}
		}
		if (advices.isEmpty()) {
			if (override != null) {
				return new NoopOverrideAdvisor(override);
			}
			return NoopAdvisor.INSTANCE;
		}
		if (override != null) {
			return new OverrideAdvisor(new DefaultAdvisor(method, advices), method, override);
		}
		return new DefaultAdvisor(method, advices);
	}

	public Advisor getAdvisor(Method method) {
		return methodCache.computeIfAbsent(method, k -> generateAdvisor(method));
	}


	@Override
	public Object intercept(Object proxy, Method method, Object[] args, Invocation invocation) throws Throwable {
		Advisor advisor = getAdvisor(method);
		return advisor.advise(proxy, target, method, args, invocation);
	}


	Map<Method, MethodHandle> getTargetMethods() {
		return targetMethods;
	}

	Map<Signature, List<Tuple2<Method, MethodHandle>>> getMatchedMethods() {
		return matchedMethods;
	}

	private void getAllAccessibleMethods(Class<?> type, Map<Method, MethodHandle> allMethods, MethodHandles.Lookup lookup) {
		boolean isInterface = type.isInterface();
		if (!isInterface) {
			// 非接口类，递归收集所有父类的方法
			Class<?> nextClass = type;
			while (nextClass != null) { // 也包含 Object 类的方法
				recursiveAddMethodsToMap(nextClass, allMethods, lookup);
				nextClass = nextClass.getSuperclass();
			}
		} else {
			// 接口类，只收集接口本身的方法
			recursiveAddMethodsToMap(type, allMethods, lookup);
		}
	}

	private void recursiveAddMethodsToMap(Class<?> interfaceType, Map<Method, MethodHandle> methods, MethodHandles.Lookup lookup) {
		Method[] declaredMethods = interfaceType.getDeclaredMethods();
		for (int i = 0, n = declaredMethods.length; i < n; i++) {
			Method method = declaredMethods[i];
			int modifiers = method.getModifiers();
			// 跳过私有方法
			if (Modifier.isPrivate(modifiers)) {
				continue;
			}
			// 忽略 Object 方法
			if (Reflects.isFinalizeMethod(method) ||
				Reflects.isNotifyMethod(method) || Reflects.isNotifyAllMethod(method) ||
				Reflects.isWaitMethod(method)) {
				continue;
			}
			// 忽略非覆写的 clone 方法
			if (Reflects.isCloneMethod(method) && method.getDeclaringClass() == Object.class) {
				continue;
			}
			try {
				method.setAccessible(true);
				MethodHandle handle = lookup.unreflect(method);
				methods.putIfAbsent(method, handle);
			} catch (IllegalAccessException e) {
				log.error("", e);
			}
		}
		// 递归处理父接口
		for (Class<?> nextInterface : interfaceType.getInterfaces()) {
			recursiveAddMethodsToMap(nextInterface, methods, lookup);
		}
	}
}

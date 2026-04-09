package io.polaris.core.aop;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import io.polaris.core.asm.proxy.Interceptor;
import io.polaris.core.asm.proxy.Invocation;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.tuple.Tuple2;
import io.polaris.core.tuple.Tuples;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class Aspect implements Interceptor {
	private static final Logger log = Loggers.of(Aspect.class);

	private final Object target;
	private final Map<Method, Advisor> methodCache = new ConcurrentHashMap<>(32);
	private final List<MatchedAdvice> matchedAdvices = new CopyOnWriteArrayList<>();
	private final Map<Method, MethodHandle> targetMethods;
	private final Map<String, MethodHandle> matchedMethods;
	/** 重写方法生成器 */
	private Function<Method, Function<Object[], Object>> overrideGenerator;
	/** 重写方法映射表 */
	private Map<Method, Function<Object[], Object>> overrideMapper;
	/** 默认方法生成器 */
	protected Function<Method, Function<Object[], Object>> defaultGenerator;
	/** 默认方法映射表 */
	protected Map<Method, Function<Object[], Object>> defaultMapper;

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
		Map<String, MethodHandle> matchedMethods = new HashMap<>();
		allMethods.forEach((method, handle) -> {
			String key = method.getName() + Type.getMethodDescriptor(method);
			matchedMethods.putIfAbsent(key, handle);
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

	public void addAdvice(Predicate<Method> predicate, Advice... advices) {
		this.matchedAdvices.add(new MatchedAdvice(predicate, advices));
		clearCache();
	}

	public void override(Function<Method, Function<Object[], Object>> overrideGenerator) {
		this.overrideGenerator = overrideGenerator;
		clearCache();
	}

	public void override(Method method, Function<Object[], Object> overrideFunction) {
		if (this.overrideMapper == null) {
			this.overrideMapper = new ConcurrentHashMap<>();
		}
		this.overrideMapper.put(method, overrideFunction);
		clearCache();
	}

	public void override(Map<Method, Function<Object[], Object>> overrideMapper) {
		if (overrideMapper == null || overrideMapper.isEmpty()) {
			this.overrideMapper = null;
		}
		this.overrideMapper = new ConcurrentHashMap<>(overrideMapper);
		clearCache();
	}

	public void defaults(Function<Method, Function<Object[], Object>> defaultGenerator) {
		this.defaultGenerator = defaultGenerator;
		clearCache();
	}

	public void defaults(Method method, Function<Object[], Object> defaultFunction) {
		if (this.defaultMapper == null) {
			this.defaultMapper = new ConcurrentHashMap<>();
		}
		this.defaultMapper.put(method, defaultFunction);
		clearCache();
	}

	public void defaults(Map<Method, Function<Object[], Object>> defaultMapper) {
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
		Function<Object[], Object> override = null;
		// 如存在重写实现，则使用
		if (this.overrideMapper != null) {
			override = this.overrideMapper.get(method);
		}
		if (override == null && this.overrideGenerator != null) {
			override = this.overrideGenerator.apply(method);
		}
		// 不存在重写实现，且目标类不存在此方法，则尝试使用默认实现
		if (override == null) {
			if (this.defaultMapper != null || this.defaultGenerator != null) {
				if (!this.targetMethods.containsKey(method)
					&& !this.matchedMethods.containsKey(method.getName() + Type.getMethodDescriptor(method))) {
					// 方法不存在，尝试使用默认方法
					Function<Object[], Object> defaultFunction = null;
					if (this.defaultMapper != null) {
						defaultFunction = this.defaultMapper.get(method);
					}
					if (defaultFunction == null && this.defaultGenerator != null) {
						defaultFunction = this.defaultGenerator.apply(method);
					}

					if (defaultFunction != null) {
						override = defaultFunction;
					}
				}
			}
		}

		// 获取匹配的拦截器，生成织入方法
		List<Advice> advices = new ArrayList<>();
		for (MatchedAdvice matchedAdvice : matchedAdvices) {
			if (matchedAdvice.accept(method)) {
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
		return advisor.advise(target, args, invocation);
	}


	Map<Method, MethodHandle> getTargetMethods() {
		return targetMethods;
	}

	Map<String, MethodHandle> getMatchedMethods() {
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

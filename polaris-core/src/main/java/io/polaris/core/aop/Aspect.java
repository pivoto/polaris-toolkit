package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import io.polaris.core.asm.proxy.Interceptor;
import io.polaris.core.asm.proxy.Invocation;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class Aspect implements Interceptor {

	private final Object target;
	private final Map<MethodCacheKey, Advisor> methodCache = new ConcurrentHashMap<>(32);
	private final List<MatchedAdvice> matchedAdvices = new ArrayList<>();

	public Aspect(Object target, Advice... advices) {
		this(target, Arrays.asList(advices));
	}

	public Aspect(Object target, Iterable<Advice> advices) {
		this.target = target;
		for (Advice advice : advices) {
			addAdvice(advice);
		}
	}

	public void addAdvice(Advice... advices) {
		this.matchedAdvices.add(new MatchedAdvice(null, advices));
		clearCache();
	}

	public void addAdvice(Predicate<Method> predicate, Advice... advices) {
		this.matchedAdvices.add(new MatchedAdvice(predicate, advices));
		clearCache();
	}

	public void clearCache() {
		methodCache.clear();
	}

	public Advisor getAdvisor(Method method) {
		MethodCacheKey key = new MethodCacheKey(method);
		return methodCache.computeIfAbsent(key, k -> {
			List<Advice> advices = new ArrayList<>();
			for (MatchedAdvice matchedAdvice : matchedAdvices) {
				if (matchedAdvice.accept(method)) {
					advices.addAll(Arrays.asList(matchedAdvice.getAdvices()));
				}
			}
			if (advices.isEmpty()) {
				return NoopAdvisor.INSTANCE;
			}
			return new DefaultAdvisor(method, advices);
		});
	}


	@Override
	public Object intercept(Object obj, Method method, Object[] args, Invocation invocation) throws Throwable {
		Advisor advisor = getAdvisor(method);
		return advisor.advise(target, args, invocation);
	}


	private static final class MethodCacheKey implements Comparable<MethodCacheKey> {
		private final String name;
		private final String desc;

		public MethodCacheKey(Method method) {
			this.name = method.getName();
			this.desc = Type.getMethodDescriptor(method);
		}

		@Override
		public int compareTo(MethodCacheKey other) {
			int result = this.name.compareTo(other.name);
			if (result == 0) {
				result = this.desc.compareTo(other.desc);
			}
			return result;
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof MethodCacheKey)) return false;
			MethodCacheKey that = (MethodCacheKey) o;
			return Objects.equals(name, that.name) && Objects.equals(desc, that.desc);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, desc);
		}
	}
}

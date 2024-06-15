package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import lombok.Getter;

/**
 * @author Qt
 * @since May 12, 2024
 */
@Getter
public class MatchedInterceptor {

	private final Predicate<Method> predicate;
	private final Interceptor[] interceptors;

	public MatchedInterceptor(Predicate<Method> predicate, Interceptor... interceptors) {
		this.predicate = predicate;
		this.interceptors = interceptors;
	}

	public boolean accept(Method method) {
		return predicate == null || predicate.test(method);
	}
}

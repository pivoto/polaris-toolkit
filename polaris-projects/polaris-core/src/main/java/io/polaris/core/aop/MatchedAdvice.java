package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import lombok.Getter;

/**
 * @author Qt
 * @since May 14, 2024
 */
@Getter
public class MatchedAdvice {

	private final Predicate<Method> predicate;
	private final Advice[] advices;

	public MatchedAdvice(Predicate<Method> predicate, Advice... advices) {
		this.predicate = predicate;
		this.advices = advices;
	}

	public boolean accept(Method method) {
		return predicate == null || predicate.test(method);
	}
}

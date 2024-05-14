package io.polaris.core.aop;

import java.lang.reflect.Method;

import io.polaris.core.tuple.ValueRef;

/**
 * @author Qt
 * @since May 13, 2024
 */
public interface FixedAdvice extends Advice {

	ValueRef<?> fixedValue(Object target, Method method, Object[] args);

}

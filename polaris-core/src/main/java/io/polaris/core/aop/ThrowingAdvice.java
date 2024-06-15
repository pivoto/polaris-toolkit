package io.polaris.core.aop;

import java.lang.reflect.Method;

/**
 * @author Qt
 * @since May 13, 2024
 */
public interface ThrowingAdvice extends Advice {

	void throwing(Object target, Method method, Object[] args, Throwable e) throws Throwable;

}

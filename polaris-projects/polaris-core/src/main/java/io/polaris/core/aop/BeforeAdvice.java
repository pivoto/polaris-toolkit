package io.polaris.core.aop;

import java.lang.reflect.Method;

/**
 * @author Qt
 * @since May 13, 2024
 */
public interface BeforeAdvice extends Advice {

	void before(Object target, Method method, Object[] args) throws Throwable;

}

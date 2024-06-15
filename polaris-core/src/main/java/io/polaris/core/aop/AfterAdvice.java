package io.polaris.core.aop;

import java.lang.reflect.Method;

/**
 * @author Qt
 * @since May 13, 2024
 */
public interface AfterAdvice extends Advice {

	void after(Object target, Method method, Object[] args, Object retVal) throws Throwable;

}

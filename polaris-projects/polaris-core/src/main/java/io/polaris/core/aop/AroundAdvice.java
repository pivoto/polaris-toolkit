package io.polaris.core.aop;

import java.lang.reflect.Method;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since May 13, 2024
 */
public interface AroundAdvice extends Advice {

	Object around(Object target, Method method, Object[] args, Invocation invocation) throws Throwable;

}

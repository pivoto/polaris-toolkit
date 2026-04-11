package io.polaris.core.aop;

import java.lang.reflect.Method;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since May 14, 2024
 */
public interface Advisor {


	Object advise(Object proxy, Object target, Method method, Object[] args, Invocation invocation) throws Throwable;


}

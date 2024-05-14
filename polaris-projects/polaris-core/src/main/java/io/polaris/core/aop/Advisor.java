package io.polaris.core.aop;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since May 14, 2024
 */
public interface Advisor  {


	Object advise(Object target, Object[] args, Invocation invocation) throws Throwable;


}

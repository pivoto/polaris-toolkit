package io.polaris.core.aop;

import java.lang.reflect.Method;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since May 14, 2024
 */
class NoopAdvisor implements Advisor {
	public static final NoopAdvisor INSTANCE = new NoopAdvisor();

	@Override
	public Object advise(Object proxy, Object target, Method method, Object[] args, Invocation invocation) throws Throwable {
		return invocation.invoke(target, args);
	}
}

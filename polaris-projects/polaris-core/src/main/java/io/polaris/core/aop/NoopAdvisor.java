package io.polaris.core.aop;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class NoopAdvisor implements Advisor {
	public static final NoopAdvisor INSTANCE = new NoopAdvisor();

	@Override
	public Object advise(Object target, Object[] args, Invocation invocation) throws Throwable {
		return invocation.invoke(target, args);
	}
}

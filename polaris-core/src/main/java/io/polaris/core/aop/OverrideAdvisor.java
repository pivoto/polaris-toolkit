package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.function.Function;

import javax.annotation.Nonnull;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since Apr 08, 2026
 */
class OverrideAdvisor implements Advisor {
	@Nonnull
	private final Advisor advisor;
	@Nonnull
	private final Method method;
	@Nonnull
	private final Function<Object[], Object> override;

	public OverrideAdvisor(@Nonnull Advisor advisor, @Nonnull Method method, @Nonnull Function<Object[], Object> override) {
		this.advisor = advisor;
		this.method = method;
		this.override = override;
	}

	@Override
	public Object advise(Object target, Object[] args, Invocation invocation) throws Throwable {
		invocation = new OverrideInvocation(invocation.getProxy(), method, override);
		return advisor.advise(target, args, invocation);
	}
}

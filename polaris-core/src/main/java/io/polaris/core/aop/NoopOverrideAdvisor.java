package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.function.Function;

import javax.annotation.Nonnull;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since Apr 08, 2026
 */
class NoopOverrideAdvisor implements Advisor {
	@Nonnull
	private final Function<Object[], Object> override;

	public NoopOverrideAdvisor(@Nonnull Function<Object[], Object> override) {
		this.override = override;
	}


	@Override
	public Object advise(Object target, Object[] args, Invocation invocation) throws Throwable {
		return override.apply(args);
	}
}

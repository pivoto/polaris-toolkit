package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.function.Function;

import javax.annotation.Nonnull;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since Apr 08, 2026
 */
class OverrideInvocation implements Invocation {
	@Nonnull
	private final Object proxy;
	@Nonnull
	private final Method method;
	@Nonnull
	private final Function<Object[], Object> override;

	public OverrideInvocation(@Nonnull Object proxy, @Nonnull Method method, @Nonnull Function<Object[], Object> override) {
		this.proxy = proxy;
		this.method = method;
		this.override = override;
	}

	@Nonnull
	@Override
	public Object getProxy() {
		return proxy;
	}

	@Nonnull
	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public Object invoke(Object target, Object[] args) throws Throwable {
		return override.apply(args);
	}
}

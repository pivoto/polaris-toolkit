package io.polaris.core.aop;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import io.polaris.core.asm.proxy.Invocation;
import io.polaris.core.function.ThrowableSupplierWithArgs1;

/**
 * @author Qt
 * @since Apr 08, 2026
 */
class DelegateInvocation implements Invocation {
	@Nonnull
	private final Object proxy;
	@Nonnull
	private final Method method;
	@Nonnull
	private final ThrowableSupplierWithArgs1<Object[], Object> delegate;

	public DelegateInvocation(@Nonnull Object proxy, @Nonnull Method method, @Nonnull ThrowableSupplierWithArgs1<Object[], Object> delegate) {
		this.proxy = proxy;
		this.method = method;
		this.delegate = delegate;
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
		return delegate.get(args);
	}
}

package io.polaris.core.aop;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since May 14, 2024
 */
class JdkInvocation implements Invocation {
	private final Object proxy;
	private final Method method;

	public JdkInvocation(Object proxy, Method method) {
		this.proxy = proxy;
		this.method = method;
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
		return method.invoke(target, args);
	}
}

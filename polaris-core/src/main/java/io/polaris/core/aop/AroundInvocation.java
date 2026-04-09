package io.polaris.core.aop;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since May 14, 2024
 */
class AroundInvocation implements Invocation {

	private final Invocation invocation;
	private final AroundAdvice[] advices;
	private final int index;
	private final Method method;

	public AroundInvocation(Invocation invocation, AroundAdvice[] advices, int index, Method method) {
		if (index < 0) {
			throw new IllegalArgumentException("index < 0");
		}
		this.invocation = invocation;
		this.advices = advices;
		this.index = index;
		this.method = method;
	}

	@Nonnull
	@Override
	public Object getProxy() {
		return invocation.getProxy();
	}

	@Nonnull
	@Override
	public Method getMethod() {
		return invocation.getMethod();
	}

	@Override
	public Object invoke(Object target, Object[] args) throws Throwable {
		int size = advices.length;
		if (index < size) {
			AroundAdvice advice = advices[index];
			if (index < size - 1) {
				return advice.around(target, method, args, new AroundInvocation(invocation, advices, index + 1, method));
			} else {
				return advice.around(target, method, args, invocation);
			}
		} else {
			return invocation.invoke(target, args);
		}
	}
}

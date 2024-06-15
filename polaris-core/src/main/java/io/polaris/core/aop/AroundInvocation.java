package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.List;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class AroundInvocation implements Invocation {

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

	@Override
	public Object getProxy() {
		return invocation.getProxy();
	}

	@Override
	public Method getMethod() {
		return invocation.getMethod();
	}

	@Override
	public Object invoke(Object obj, Object[] args) throws Throwable {
		int size = advices.length;
		if (index < size) {
			AroundAdvice advice = advices[index];
			if (index < size - 1) {
				return advice.around(obj, method, args, new AroundInvocation(invocation, advices, index + 1, method));
			} else {
				return advice.around(obj, method, args, invocation);
			}
		} else {
			return invocation.invoke(obj, args);
		}
	}
}

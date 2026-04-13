package io.polaris.core.aop;

import java.lang.reflect.Method;

import javax.annotation.concurrent.NotThreadSafe;

import io.polaris.core.asm.proxy.Invocation;

/**
 * @author Qt
 * @since May 14, 2024
 */
@NotThreadSafe
class AroundInvocation implements Invocation {

	private final Invocation invocation;
	private final AroundAdvice[] advices;
	private final Method method;
	private final Object proxy;
	private int index;

	AroundInvocation(Invocation invocation, AroundAdvice[] advices, int index, Object proxy,Method method) {
		if (index < 0) {
			throw new IllegalArgumentException("index < 0");
		}
		this.invocation = invocation;
		this.advices = advices;
		this.index = index;
		this.proxy = proxy;
		this.method = method;
	}

	@Override
	public Object invoke(Object target, Object[] args) throws Throwable {
		int size = advices.length;
		int index = this.index;
		if (index < size) {
			AroundAdvice advice = advices[index];
			if (index < size - 1) {
				//return advice.around(target, method, args, new AroundInvocation(invocation, advices, index + 1, method));
				// 不再生成新的AroundInvocation，通过更新index来完成拦截链路
				this.index++;
				return advice.around(proxy, target, method, args, this);
			} else {
				return advice.around(proxy, target, method, args, invocation);
			}
		} else {
			return invocation.invoke(target, args);
		}
	}
}

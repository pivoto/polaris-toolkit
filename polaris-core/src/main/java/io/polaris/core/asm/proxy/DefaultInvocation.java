package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;

import lombok.Getter;

public class DefaultInvocation implements Invocation {

	private final int index;
	@Getter
	private final Object proxy;
	@Getter
	private final Method method;
	private final Invoker rawInvoker;
	private final Invoker superInvoker;

	public DefaultInvocation(int index, Object proxy, Method method, Invoker rawInvoker, Invoker superInvoker) {
		this.index = index;
		this.proxy = proxy;
		this.method = method;
		this.rawInvoker = rawInvoker;
		this.superInvoker = superInvoker;
	}

	public Object invoke(Object obj, Object[] args) throws Throwable {
		if (proxy != obj) {
			return rawInvoker.invoke(index, obj, args);
		} else {
			return superInvoker.invoke(index, obj, args);
		}
	}

}

package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;

import lombok.Getter;

public class BulkInvocation implements Invocation {

	private final int index;
	@Getter
	private final Object proxy;
	@Getter
	private final Method method;
	private final Invoker rawInvoker;
	private final Invoker superInvoker;
	private final Interceptor[] interceptors;
	private int cursor = 0;

	public BulkInvocation(int index, Object proxy, Method method, Invoker rawInvoker, Invoker superInvoker, Interceptor[] interceptors) {
		this.index = index;
		this.proxy = proxy;
		this.method = method;
		this.rawInvoker = rawInvoker;
		this.superInvoker = superInvoker;
		this.interceptors = interceptors;
	}

	public Object invoke(Object obj, Object[] args) throws Throwable {
		if (cursor < interceptors.length) {
			// 继续下一个拦截器
			return interceptors[cursor++].intercept(obj, method, args, this);
		}
		if (proxy != obj) {
			return rawInvoker.invoke(index, obj, args);
		} else {
			return superInvoker.invoke(index, obj, args);
		}
	}

}

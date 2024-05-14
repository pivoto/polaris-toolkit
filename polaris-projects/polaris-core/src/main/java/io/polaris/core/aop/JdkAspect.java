package io.polaris.core.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class JdkAspect extends Aspect implements InvocationHandler {


	public JdkAspect(Object target, Advice... advices) {
		super(target, advices);
	}

	public JdkAspect(Object target, Iterable<Advice> advices) {
		super(target, advices);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		JdkInvocation invocation = new JdkInvocation(proxy, method);
		return super.intercept(proxy, method, args, invocation);
	}
}

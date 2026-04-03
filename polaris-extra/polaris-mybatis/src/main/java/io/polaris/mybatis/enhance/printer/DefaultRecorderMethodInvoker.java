package io.polaris.mybatis.enhance.printer;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * @author Qt
 * @since Apr 03, 2026
 */
class DefaultRecorderMethodInvoker implements RecorderMapperMethodInvoker {
	private final MethodHandle methodHandle;

	public DefaultRecorderMethodInvoker(MethodHandle methodHandle) {
		super();
		this.methodHandle = methodHandle;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return methodHandle.bindTo(proxy).invokeWithArguments(args);
	}
}

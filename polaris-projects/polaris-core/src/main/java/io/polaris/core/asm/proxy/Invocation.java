package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;

public interface Invocation {

	Object getProxy();

	Method getMethod();

	Object invoke(Object obj, Object[] args) throws Throwable;

}

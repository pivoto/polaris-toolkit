package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;


public interface Interceptor {

	Object intercept(Object obj, Method method, Object[] args, Invocation invocation) throws Throwable;

}

package io.polaris.core.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import io.polaris.core.asm.internal.AsmConsts;
import io.polaris.core.asm.proxy.Enhancer;
import io.polaris.core.asm.proxy.Interceptor;

/**
 * @author Qt
 * @since May 13, 2024
 */
public class ProxyUtils {

	public static <T> ProxyFactory proxyFactory(Object target) {
		return new AsmProxyFactory(target);
	}

	public static <T> ProxyFactory jdkProxyFactory(Object target) {
		return new JdkProxyFactory(target);
	}

	@SuppressWarnings("unchecked")
	public static <T> T proxy(Class<?> superclass, Class<?>[] interfaces, Interceptor interceptor) {
		return (T) proxy(Thread.currentThread().getContextClassLoader(), superclass, interfaces, interceptor);
	}

	@SuppressWarnings("unchecked")
	public static <T> T proxy(ClassLoader classloader, Class<?> superclass, Class<?>[] interfaces, Interceptor interceptor) {
		Enhancer enhancer = new Enhancer();
		enhancer.setClassLoader(classloader);
		return (T) enhancer.superclass(superclass)
			.interfaces(appendInterfaces(interfaces))
			.interceptor(interceptor)
			.create();
	}

	@SuppressWarnings("unchecked")
	public static <T> T jdkProxy(Class<?>[] interfaces, InvocationHandler handler) {
		return (T) jdkProxy(null, interfaces, handler);
	}


	@SuppressWarnings("unchecked")
	public static <T> T jdkProxy(ClassLoader classloader, Class<?>[] interfaces, InvocationHandler handler) {
		if (classloader == null) {
			classloader = Thread.currentThread().getContextClassLoader();
		}
		return (T) Proxy.newProxyInstance(classloader, appendInterfaces(interfaces), handler);
	}

	private static Class<?>[] appendInterfaces(Class<?>[] interfaces) {
		if (interfaces == null || interfaces.length == 0) {
			return new Class[]{AopProxy.class};
		}
		Class<?>[] array = new Class[interfaces.length + 1];
		System.arraycopy(interfaces, 0, array, 0, interfaces.length);
		array[interfaces.length] = AopProxy.class;
		return array;
	}


	public static boolean isAopProxy(Object object) {
		return (object instanceof AopProxy) &&
			(Proxy.isProxyClass(object.getClass()) ||
				object.getClass().getName().contains(AsmConsts.CLASS_TAG_SEPARATOR));
	}

	public static boolean isJdkDynamicProxy(Object object) {
		return (object instanceof AopProxy && Proxy.isProxyClass(object.getClass()));
	}

	public static boolean isAsmDynamicProxy(Object object) {
		return (object instanceof AopProxy && object.getClass().getName().contains(AsmConsts.CLASS_TAG_SEPARATOR));
	}
}

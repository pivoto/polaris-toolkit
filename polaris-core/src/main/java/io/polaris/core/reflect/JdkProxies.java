package io.polaris.core.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.polaris.core.err.InvocationException;

/**
 * @author Qt
 * @since Apr 04, 2026
 */
public class JdkProxies {

	// @see java.lang.invoke.MethodHandleProxies

	public static <T> T asInterfaceInstance(Class<T> intfc, Function<Method, Function<Object[], Object>> functionGenerator) throws IllegalAccessException {
		return asInterfaceInstance(intfc, null, true, functionGenerator);
	}

	public static <T> T asInterfaceInstance(Class<T> intfc, Class<?>[] otherInterfaces, Function<Method, Function<Object[], Object>> functionGenerator) throws IllegalAccessException {
		return asInterfaceInstance(intfc, otherInterfaces, true, functionGenerator);
	}

	public static <T> T asInterfaceInstance(Class<T> intfc, boolean cached, Function<Method, Function<Object[], Object>> functionGenerator) throws IllegalAccessException {
		return asInterfaceInstance(intfc, null, cached, functionGenerator);
	}

	public static <T> T asInterfaceInstance(Class<T> intfc, Class<?>[] otherInterfaces, boolean cached, Function<Method, Function<Object[], Object>> functionGenerator) throws IllegalAccessException {
		if (!intfc.isInterface() || !Modifier.isPublic(intfc.getModifiers())) {
			throw new IllegalArgumentException("required public interface");
		}
		if (otherInterfaces != null) {
			for (Class<?> otherInterface : otherInterfaces) {
				if (!otherInterface.isInterface() || !Modifier.isPublic(otherInterface.getModifiers())) {
					throw new IllegalArgumentException("required public interface");
				}
			}
		}
		ClassLoader proxyLoader = intfc.getClassLoader();
		if (proxyLoader == null) {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			proxyLoader = cl != null ? cl : ClassLoader.getSystemClassLoader();
		}
		Map<Method, MethodHandle> methodHandleMap = getMethodHandleMap(intfc, otherInterfaces);
		Map<Method, Function<Object[], Object>> methodFunctions = new HashMap<>();
		if (cached) {
			for (Method method : methodHandleMap.keySet()) {
				Function<Object[], Object> func = functionGenerator.apply(method);
				if (func != null) {
					methodFunctions.put(method, func);
				}
			}
		}
		final InvocationHandler ih = (proxy, method, args) -> {
			if (isObjectMethod(method)) {
				return callObjectMethod(proxy, method, args);
			}
			if (method.isDefault()) {
				try {
					MethodHandle methodHandle = methodHandleMap.get(method);
					return methodHandle.bindTo(proxy).invokeWithArguments(args);
				} catch (ReflectiveOperationException e) {
					throw new InvocationException(e);
				}
			}

			Function<Object[], Object> func;
			if (cached) {
				func = methodFunctions.get(method);
				if (func != null) {
					return func.apply(args);
				}
			}
			func = functionGenerator.apply(method);
			if (func != null) {
				return func.apply(args);
			}
			throw new IllegalStateException("Unexpected method: " + method);
		};
		final Object proxy;
		if (System.getSecurityManager() != null) {
			final ClassLoader loader = proxyLoader;
			proxy = AccessController.doPrivileged((PrivilegedAction<Object>) () ->
				Proxy.newProxyInstance(loader, new Class<?>[]{intfc}, ih));
		} else {
			proxy = Proxy.newProxyInstance(proxyLoader, new Class<?>[]{intfc}, ih);
		}
		return intfc.cast(proxy);
	}

	private static Map<Method, MethodHandle> getMethodHandleMap(Class<?> intfc, Class<?>[] otherInterfaces) throws IllegalAccessException {
		Map<Method, MethodHandle> methodHandleMap = new HashMap<>();
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		for (Method method : intfc.getMethods()) {
			if (isObjectMethod(method)) continue;
			if (!Modifier.isAbstract(method.getModifiers())) continue;
			MethodHandle handle = lookup.unreflect(method);
			methodHandleMap.putIfAbsent(method, handle);
		}
		if (otherInterfaces != null) {
			for (Class<?> c : otherInterfaces) {
				for (Method method : c.getMethods()) {
					if (isObjectMethod(method)) continue;
					if (!Modifier.isAbstract(method.getModifiers())) continue;
					MethodHandle handle = lookup.unreflect(method);
					methodHandleMap.putIfAbsent(method, handle);
				}
			}
		}
		return methodHandleMap;
	}

	private static boolean isObjectMethod(Method m) {
		switch (m.getName()) {
			case "toString":
				return (m.getReturnType() == String.class
					&& m.getParameterTypes().length == 0);
			case "hashCode":
				return (m.getReturnType() == int.class
					&& m.getParameterTypes().length == 0);
			case "equals":
				return (m.getReturnType() == boolean.class
					&& m.getParameterTypes().length == 1
					&& m.getParameterTypes()[0] == Object.class);
		}
		return false;
	}

	private static Object callObjectMethod(Object self, Method m, Object[] args) {
		assert (isObjectMethod(m)) : m;
		switch (m.getName()) {
			case "toString":
				return self.getClass().getName() + "@" + Integer.toHexString(self.hashCode());
			case "hashCode":
				return System.identityHashCode(self);
			case "equals":
				return (self == args[0]);
		}
		return null;
	}

}

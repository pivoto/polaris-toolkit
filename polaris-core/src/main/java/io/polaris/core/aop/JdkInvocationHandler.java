package io.polaris.core.aop;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.polaris.core.asm.proxy.Invocation;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Apr 09, 2026
 */

public class JdkInvocationHandler implements InvocationHandler {
	private static final Logger log = Loggers.of(JdkInvocationHandler.class);
	private final Object target;
	private final Aspect aspect;
	private Class<?>[] interfaces;
	private final Map<Method, MethodHandle> targetMethods;
	private final Map<String, MethodHandle> matchedMethods;
	private final Map<Method, MethodHandle> undeclaredMethods = new ConcurrentHashMap<>();


	public JdkInvocationHandler(Object target, Aspect aspect, Class<?>[] interfaces) {
		this.target = target;
		this.aspect = aspect;
		interfaces = appendInterfaces(interfaces);
		this.interfaces = interfaces;

		this.targetMethods = aspect.getTargetMethods();
		this.matchedMethods = aspect.getMatchedMethods();
	}

	void interfaces(Class<?>[] interfaces) {
		this.interfaces = appendInterfaces(interfaces);
	}

	public Object getTarget() {
		return target;
	}

	public Class<?>[] getInterfaces() {
		return interfaces;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Invocation invocation = null;
		MethodHandle methodHandle = targetMethods.get(method);
		if (methodHandle != null) {
			// 存在实现方法
			invocation = new DelegateInvocation(proxy, method, (mArgs) ->
				methodHandle.bindTo(target).invokeWithArguments(mArgs));
		} else {
			MethodHandle matched = matchedMethods.get(method.getName() + Type.getMethodDescriptor(method));
			if (matched != null) {
				// 存在可借用的方法
				invocation = new DelegateInvocation(proxy, method, (mArgs) ->
					matched.bindTo(target).invokeWithArguments(mArgs));
			} else if (method.isDefault()) {
				// 属于接口默认方法
				MethodHandle undeclaredMethod = undeclaredMethods.get(method);
				if (undeclaredMethod == null) {
					try {
						method.setAccessible(true);
						undeclaredMethod = MethodHandles.lookup().unreflect(method);
						undeclaredMethods.putIfAbsent(method, undeclaredMethod);
					} catch (IllegalAccessException e) {
						log.error("", e);
					}
				}
				if (undeclaredMethod != null) {
					MethodHandle finalUndeclaredMethod = undeclaredMethod;
					invocation = new DelegateInvocation(proxy, method, (mArgs) ->
						finalUndeclaredMethod.bindTo(proxy).invokeWithArguments(mArgs));
				} else {
					invocation = new DelegateInvocation(proxy, method, (mArgs) ->
						MethodHandles.lookup().unreflect(method).bindTo(proxy).invokeWithArguments(mArgs));
				}
			}
		}
		if (invocation == null) {
			if (method.getDeclaringClass().isAssignableFrom(target.getClass())) {
				invocation = new JdkInvocation(proxy, method);
			} else {
				invocation = new DelegateInvocation(proxy, method, (mArgs) -> null);
			}
		}
		return aspect.intercept(proxy, method, args, invocation);
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

}

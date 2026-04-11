package io.polaris.core.aop;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.polaris.core.asm.internal.Signature;
import io.polaris.core.asm.proxy.Invocation;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.tuple.Tuple2;

/**
 * @author Qt
 * @since Apr 09, 2026
 */

public class JdkInvocationHandler implements InvocationHandler {
	private static final Logger log = Loggers.of(JdkInvocationHandler.class);
	private final Object target;
	private final Aspect aspect;
	private final Class<?>[] interfaces;
	private final Map<Method, MethodHandle> targetMethods;
	private final Map<Signature, List<Tuple2<Method, MethodHandle>>> matchedMethods;
	private final Map<Method, MethodHandle> undeclaredMethods = new ConcurrentHashMap<>();


	public JdkInvocationHandler(Object target, Aspect aspect, Class<?>[] interfaces) {
		this.target = target;
		this.aspect = aspect;
		Set<Class<?>> set = new LinkedHashSet<>();
		if (interfaces != null && interfaces.length > 0) {
			set.addAll(Arrays.asList(interfaces));
		}
		set.addAll(Arrays.asList(target.getClass().getInterfaces()));
		set.add(AopProxy.class);
		this.interfaces = set.toArray(new Class[0]);
		this.targetMethods = aspect.getTargetMethods();
		this.matchedMethods = aspect.getMatchedMethods();
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
		Signature signature = Signature.of(method);
		MethodHandle methodHandle = targetMethods.get(method);
		if (methodHandle != null) {
			// 存在实现方法
			invocation = Invocation.ofThrowableFunction((mArgs) ->
				methodHandle.bindTo(target).invokeWithArguments(mArgs));
		} else {
			List<Tuple2<Method, MethodHandle>> list = matchedMethods.get(signature);
			if (list != null && !list.isEmpty()) {
				// 存在可借用的方法
				Tuple2<Method, MethodHandle> tuple = list.get(0);
				MethodHandle matched = tuple.getSecond();
				invocation = Invocation.ofThrowableFunction((mArgs) ->
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
					invocation = Invocation.ofThrowableFunction((mArgs) ->
						finalUndeclaredMethod.bindTo(proxy).invokeWithArguments(mArgs));
				} else {
					invocation = Invocation.ofThrowableFunction((mArgs) ->
						MethodHandles.lookup().unreflect(method).bindTo(proxy).invokeWithArguments(mArgs));
				}
			}
		}
		// 找不到直接匹配的处理器，则使用默认处理器，如果是继承关系，则使用JdkInvocation，否则使用空实现
		if (invocation == null) {
			if (method.getDeclaringClass().isAssignableFrom(target.getClass())) {
				invocation = Invocation.ofMethod(method);
			} else {
				invocation = Invocation.noop();
			}
		}
		return aspect.intercept(proxy, method, args, invocation);
	}

}

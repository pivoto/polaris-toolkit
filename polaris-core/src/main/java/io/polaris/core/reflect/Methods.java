package io.polaris.core.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Qt
 * @since Oct 08, 2024
 */
@SuppressWarnings("all")
public class Methods {
	private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
		| MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;
	private static final Constructor<MethodHandles.Lookup> lookupConstructor;
	private static final Method privateLookupInMethod;

	static {
		Method privateLookupIn;
		try {
			privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
		} catch (NoSuchMethodException e) {
			privateLookupIn = null;
		}
		privateLookupInMethod = privateLookupIn;
		Constructor<MethodHandles.Lookup> lookup = null;
		if (privateLookupInMethod == null) {
			// JDK 1.8
			try {
				lookup = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
				lookup.setAccessible(true);
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException(
					"There is neither 'privateLookupIn(Class, Lookup)' nor 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.",
					e);
			} catch (Exception e) {
				lookup = null;
			}
		}
		lookupConstructor = lookup;
	}

	public static <T> MethodHandles.Lookup lookup(Class<T> declaringClass) {
		try {
			if (privateLookupInMethod == null) {
				return lookupConstructor.newInstance(declaringClass, ALLOWED_MODES);
			} else {
				return ((MethodHandles.Lookup) privateLookupInMethod.invoke(null, declaringClass, MethodHandles.lookup()));
			}
		} catch (Exception e) {
			return MethodHandles.lookup();
		}
	}

	public static <T> MethodHandle lookup(Method method) throws IllegalAccessException {
		final Class<?> declaringClass = method.getDeclaringClass();
		try {
			if (privateLookupInMethod == null) {
				return lookupConstructor.newInstance(declaringClass, ALLOWED_MODES).unreflectSpecial(method, declaringClass);
			} else {
				return ((MethodHandles.Lookup) privateLookupInMethod.invoke(null, declaringClass, MethodHandles.lookup())).findSpecial(
					declaringClass, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
					declaringClass);
			}
		} catch (Exception e) {
			return MethodHandles.lookup().unreflectSpecial(method, declaringClass);
		}
	}


	public static <T> T newInstance(Class<T> clazz) throws Throwable {
		MethodHandle handle = findConstructor(clazz);
		Object rs = handle.invoke();
		return (T) rs;
	}

	public static <T> T newInstance(Class<T> clazz, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = findConstructor(clazz, parameterTypes);
		Object rs = handle.invokeWithArguments(params);
		return (T) rs;
	}

	public static <T> T newPrivateInstance(Class<T> clazz) throws Throwable {
		MethodHandle handle = findPrivateConstructor(clazz);
		Object rs = handle.invoke();
		return (T) rs;
	}

	public static <T> T newPrivateInstance(Class<T> clazz, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = findPrivateConstructor(clazz, parameterTypes);
		Object rs = handle.invokeWithArguments(params);
		return (T) rs;
	}

	public static void invoke(Object instance, String methodName) throws Throwable {
		Class<?> instanceType = instance.getClass();
		invoke(instanceType, instance, methodName);
	}

	public static void invoke(Class<?> instanceType, Object instance, String methodName) throws Throwable {
		MethodHandle handle = findVirtual(instanceType, methodName);
		handle.invoke(instance);
	}

	public static void invoke(Object instance, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		invoke(instance.getClass(), instance, methodName, parameterTypes, params);
	}

	public static void invoke(Class<?> instanceType, Object instance, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = findVirtual(instanceType, methodName, parameterTypes);
		Object[] args = new Object[params.length + 1];
		args[0] = instance;
		System.arraycopy(params, 0, args, 1, params.length);
		handle.invokeWithArguments(args);
	}

	public static <T> T invoke(Object instance, String methodName, Class<T> returnType) throws Throwable {
		return invoke(instance.getClass(), instance, methodName, returnType);
	}

	public static <T> T invoke(Class<?> instanceType, Object instance, String methodName, Class<T> returnType) throws Throwable {
		MethodHandle handle = findVirtual(instanceType, methodName, returnType);
		Object rs = handle.invoke(instance);
		return (T) rs;
	}


	public static <T> T invoke(Object instance, String methodName, Class<T> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		return invoke(instance.getClass(), instance, methodName, returnType, parameterTypes, params);
	}

	public static <T> T invoke(Class<?> instanceType, Object instance, String methodName, Class<T> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = findVirtual(instanceType, methodName, returnType, parameterTypes);
		Object[] args = new Object[params.length + 1];
		args[0] = instance;
		System.arraycopy(params, 0, args, 1, params.length);
		Object rs = handle.invokeWithArguments(args);
		return (T) rs;
	}


	public static void invokePrivate(Object instance, String methodName) throws Throwable {
		invokePrivate(instance.getClass(), instance, methodName);
	}

	public static void invokePrivate(Class<?> instanceType, Object instance, String methodName) throws Throwable {
		MethodHandle handle = findPrivate(instanceType, methodName);
		handle.invoke(instance);
	}

	public static void invokePrivate(Object instance, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		invokePrivate(instance.getClass(), instance, methodName, parameterTypes, params);
	}

	public static void invokePrivate(Class<?> instanceType, Object instance, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = findPrivate(instanceType, methodName, parameterTypes);
		Object[] args = new Object[params.length + 1];
		args[0] = instance;
		System.arraycopy(params, 0, args, 1, params.length);
		handle.invokeWithArguments(args);
	}

	public static <T> T invokePrivate(Object instance, String methodName, Class<T> returnType) throws Throwable {
		return invokePrivate(instance.getClass(), instance, methodName, returnType);
	}

	public static <T> T invokePrivate(Class<?> instanceType, Object instance, String methodName, Class<T> returnType) throws Throwable {
		MethodHandle handle = findPrivate(instanceType, methodName, returnType);
		Object rs = handle.invoke(instance);
		return (T) rs;
	}


	public static <T> T invokePrivate(Object instance, String methodName, Class<T> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = findPrivate(instance.getClass(), methodName, returnType, parameterTypes);
		Object[] args = new Object[params.length + 1];
		args[0] = instance;
		System.arraycopy(params, 0, args, 1, params.length);
		Object rs = handle.invokeWithArguments(args);
		return (T) rs;
	}


	public static void invokeStatic(Class<?> clazz, String methodName) throws Throwable {
		MethodHandle handle = findStatic(clazz, methodName);
		handle.invoke();
	}

	public static void invokeStatic(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = findStatic(clazz, methodName, parameterTypes);
		handle.invokeWithArguments(params);
	}

	public static <T> T invokeStatic(Class<?> clazz, String methodName, Class<T> returnType) throws Throwable {
		MethodHandle handle = findStatic(clazz, methodName, returnType);
		Object rs = handle.invoke();
		return (T) rs;
	}

	public static <T> T invokeStatic(Class<?> clazz, String methodName, Class<T> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = findStatic(clazz, methodName, returnType, parameterTypes);
		Object rs = handle.invokeWithArguments(params);
		return (T) rs;
	}

	public static void invokePrivateStatic(Class<?> clazz, String methodName) throws Throwable {
		MethodHandle handle = findPrivateStatic(clazz, methodName);
		handle.invoke();
	}

	public static void invokePrivateStatic(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = findPrivateStatic(clazz, methodName, parameterTypes);
		handle.invokeWithArguments(params);
	}

	public static <T> T invokePrivateStatic(Class<?> clazz, String methodName, Class<T> returnType) throws Throwable {
		MethodHandle handle = findPrivateStatic(clazz, methodName, returnType);
		Object rs = handle.invoke();
		return (T) rs;
	}

	public static <T> T invokePrivateStatic(Class<?> clazz, String methodName, Class<T> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = findPrivateStatic(clazz, methodName, returnType, parameterTypes);
		Object rs = handle.invokeWithArguments(params);
		return (T) rs;
	}


	public static <T> T getField(Object instance, String fieldName, Class<T> fieldType) throws Throwable {
		return getField(instance.getClass(), instance, fieldName, fieldType);
	}


	public static <T> T getField(Class<?> instanceType, Object instance, String fieldName, Class<T> fieldType) throws Throwable {
		MethodHandle handle = findGetter(instanceType, fieldName, fieldType);
		Object rs = handle.invoke(instance);
		return (T) rs;
	}


	public static <T> T getPrivateField(Object instance, String fieldName, Class<T> fieldType) throws Throwable {
		return getPrivateField(instance.getClass(), instance, fieldName, fieldType);
	}


	public static <T> T getPrivateField(Class<?> instanceType, Object instance, String fieldName, Class<T> fieldType) throws Throwable {
		MethodHandle handle = findPrivateGetter(instanceType, fieldName, fieldType);
		Object rs = handle.invoke(instance);
		return (T) rs;
	}

	public static <T> void setField(Object instance, String fieldName, Class<T> fieldType, T value) throws Throwable {
		setField(instance.getClass(), instance, fieldName, fieldType, value);
	}

	public static <T> void setField(Class<?> instanceType, Object instance, String fieldName, Class<T> fieldType, T value) throws Throwable {
		MethodHandle handle = findSetter(instanceType, fieldName, fieldType);
		handle.invoke(instance, value);
	}

	public static <T> void setPrivateField(Object instance, String fieldName, Class<T> fieldType, T value) throws Throwable {
		setPrivateField(instance.getClass(), instance, fieldName, fieldType, value);
	}

	public static <T> void setPrivateField(Class<?> instanceType, Object instance, String fieldName, Class<T> fieldType, T value) throws Throwable {
		MethodHandle handle = findPrivateSetter(instanceType, fieldName, fieldType);
		handle.invoke(instance, value);
	}

	public static <T> T getStaticField(Class<?> clazz, String fieldName, Class<T> fieldType) throws Throwable {
		MethodHandle handle = findStaticGetter(clazz, fieldName, fieldType);
		Object rs = handle.invoke();
		return (T) rs;
	}

	public static <T> T getPrivateStaticField(Class<?> clazz, String fieldName, Class<T> fieldType) throws Throwable {
		MethodHandle handle = findPrivateStaticGetter(clazz, fieldName, fieldType);
		Object rs = handle.invoke();
		return (T) rs;
	}

	public static <T> void setStaticField(Class<?> clazz, String fieldName, Class<T> fieldType, T value) throws Throwable {
		MethodHandle handle = findStaticSetter(clazz, fieldName, fieldType);
		handle.invoke(value);
	}

	public static <T> void setPrivateStaticField(Class<?> clazz, String fieldName, Class<T> fieldType, T value) throws Throwable {
		MethodHandle handle = findPrivateStaticSetter(clazz, fieldName, fieldType);
		handle.invoke(value);
	}

	public static Class<?>[] actualParamTypes(Object[] params) {
		Class<?>[] actualParamTypes = new Class[params.length];
		for (int i = 0; i < params.length; i++) {
			actualParamTypes[i] = params[i].getClass();
		}
		return actualParamTypes;
	}

	public static MethodHandle adaptMethodHandle(MethodHandle mh, Class<?> actualReturnType, Class<?>[] actualParamTypes) {
		MethodType originalType = mh.type();
		// 构建实际参数的方法类型
		MethodType targetType = MethodType.methodType(actualReturnType, actualParamTypes);
		// 类型完全一致时，直接返回原句柄，跳过 asType
		if (originalType.equals(targetType)) {
			return mh;
		}
		// 类型不一致时，才执行 asType 适配（子类转型、装箱拆箱）
		return mh.asType(targetType);
	}

	public static <T> MethodHandle findConstructor(Class<T> clazz) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class);
		MethodHandle handle = lookup.findConstructor(clazz, methodType);
		return handle;
	}

	public static <T> MethodHandle findConstructor(Class<T> clazz, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class, parameterTypes);
		MethodHandle handle = lookup.findConstructor(clazz, methodType);
		return handle;
	}

	public static <T> MethodHandle findPrivateConstructor(Class<T> clazz) throws NoSuchMethodException, IllegalAccessException {
		Constructor<T> constructor = clazz.getDeclaredConstructor();
		constructor.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflectConstructor(constructor);
		return handle;
	}

	public static <T> MethodHandle findPrivateConstructor(Class<T> clazz, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
		constructor.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflectConstructor(constructor);
		return handle;
	}

	public static <T> MethodHandle findVirtual(Class<T> clazz, String methodName) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class);
		MethodHandle handle = lookup.findVirtual(clazz, methodName, methodType);
		return handle;
	}

	public static <T> MethodHandle findVirtual(Class<T> clazz, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class, parameterTypes);
		MethodHandle handle = lookup.findVirtual(clazz, methodName, methodType);
		return handle;
	}

	public static <T, R> MethodHandle findVirtual(Class<T> clazz, String methodName, Class<R> returnType) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(returnType);
		MethodHandle handle = lookup.findVirtual(clazz, methodName, methodType);
		return handle;
	}

	public static <T, R> MethodHandle findVirtual(Class<T> clazz, String methodName, Class<R> returnType, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(returnType, parameterTypes);
		MethodHandle handle = lookup.findVirtual(clazz, methodName, methodType);
		return handle;
	}


	public static <T> MethodHandle findPrivate(Class<T> clazz, String methodName) throws NoSuchMethodException, IllegalAccessException {
		Method method = clazz.getDeclaredMethod(methodName);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		return handle;
	}

	public static <T> MethodHandle findPrivate(Class<T> clazz, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		return handle;
	}

	public static <T, R> MethodHandle findPrivate(Class<T> clazz, String methodName, Class<R> returnType) throws NoSuchMethodException, IllegalAccessException {
		Method method = clazz.getDeclaredMethod(methodName);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		return handle;
	}

	public static <T, R> MethodHandle findPrivate(Class<T> clazz, String methodName, Class<R> returnType, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		return handle;
	}


	public static <T> MethodHandle findStatic(Class<T> clazz, String methodName) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class);
		MethodHandle handle = lookup.findStatic(clazz, methodName, methodType);
		return handle;
	}

	public static <T> MethodHandle findStatic(Class<T> clazz, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class, parameterTypes);
		MethodHandle handle = lookup.findStatic(clazz, methodName, methodType);
		return handle;
	}

	public static <T, R> MethodHandle findStatic(Class<T> clazz, String methodName, Class<R> returnType) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(returnType);
		MethodHandle handle = lookup.findStatic(clazz, methodName, methodType);
		return handle;
	}

	public static <T, R> MethodHandle findStatic(Class<T> clazz, String methodName, Class<R> returnType, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(returnType, parameterTypes);
		MethodHandle handle = lookup.findStatic(clazz, methodName, methodType);
		return handle;
	}


	public static <T> MethodHandle findPrivateStatic(Class<T> clazz, String methodName) throws NoSuchMethodException, IllegalAccessException {
		Method method = clazz.getDeclaredMethod(methodName);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		return handle;
	}

	public static <T> MethodHandle findPrivateStatic(Class<T> clazz, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		return handle;
	}

	public static <T, R> MethodHandle findPrivateStatic(Class<T> clazz, String methodName, Class<R> returnType) throws NoSuchMethodException, IllegalAccessException {
		Method method = clazz.getDeclaredMethod(methodName);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		return handle;
	}

	public static <T, R> MethodHandle findPrivateStatic(Class<T> clazz, String methodName, Class<R> returnType, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		return handle;
	}


	public static <T, R> MethodHandle findGetter(Class<T> clazz, String fieldName, Class<R> fieldType) throws NoSuchFieldException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.findGetter(clazz, fieldName, fieldType);
		return handle;
	}

	public static <T, R> MethodHandle findPrivateGetter(Class<T> clazz, String fieldName, Class<R> fieldType) throws NoSuchFieldException, IllegalAccessException {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflectGetter(field);
		return handle;
	}

	public static <T, R> MethodHandle findSetter(Class<T> clazz, String fieldName, Class<R> fieldType) throws NoSuchFieldException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.findSetter(clazz, fieldName, fieldType);
		return handle;
	}

	public static <T, R> MethodHandle findPrivateSetter(Class<T> clazz, String fieldName, Class<R> fieldType) throws NoSuchFieldException, IllegalAccessException {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflectSetter(field);
		return handle;
	}

	public static <T, R> MethodHandle findStaticGetter(Class<T> clazz, String fieldName, Class<R> fieldType) throws NoSuchFieldException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.findStaticGetter(clazz, fieldName, fieldType);
		return handle;
	}

	public static <T, R> MethodHandle findPrivateStaticGetter(Class<T> clazz, String fieldName, Class<R> fieldType) throws NoSuchFieldException, IllegalAccessException {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflectGetter(field);
		return handle;
	}

	public static <T, R> MethodHandle findStaticSetter(Class<T> clazz, String fieldName, Class<R> fieldType) throws NoSuchFieldException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.findStaticSetter(clazz, fieldName, fieldType);
		return handle;
	}

	public static <T, R> MethodHandle findPrivateStaticSetter(Class<T> clazz, String fieldName, Class<R> fieldType) throws NoSuchFieldException, IllegalAccessException {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflectSetter(field);
		return handle;
	}


}

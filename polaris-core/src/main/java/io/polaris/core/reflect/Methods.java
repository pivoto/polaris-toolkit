package io.polaris.core.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Qt
 * @since Oct 08, 2024
 */
@SuppressWarnings("unchecked")
public class Methods {


	public static <T> T newInstance(Class<T> clazz) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class);
		MethodHandle handle = lookup.findConstructor(clazz, methodType);
		Object rs = handle.invoke();
		return (T) rs;
	}

	public static <T> T newInstance(Class<T> clazz, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class, parameterTypes);
		MethodHandle handle = lookup.findConstructor(clazz, methodType);
		Object rs = handle.invokeWithArguments(params);
		return (T) rs;
	}

	public static void invoke(Object instance, String methodName) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class);
		MethodHandle handle = lookup.findVirtual(instance.getClass(), methodName, methodType);
		handle.invoke(instance);
	}

	public static void invoke(Object instance, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class, parameterTypes);
		MethodHandle handle = lookup.findVirtual(instance.getClass(), methodName, methodType);
		Object[] args = new Object[params.length + 1];
		args[0] = instance;
		System.arraycopy(params, 0, args, 1, params.length);
		handle.invokeWithArguments(args);
	}


	public static <T> T invoke(Object instance, String methodName, Class<T> returnType) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(returnType);
		MethodHandle handle = lookup.findVirtual(instance.getClass(), methodName, methodType);
		Object rs = handle.invoke(instance);
		return (T) rs;
	}


	public static <T> T invoke(Object instance, String methodName, Class<T> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(returnType, parameterTypes);
		MethodHandle handle = lookup.findVirtual(instance.getClass(), methodName, methodType);
		Object[] args = new Object[params.length + 1];
		args[0] = instance;
		System.arraycopy(params, 0, args, 1, params.length);
		Object rs = handle.invokeWithArguments(args);
		return (T) rs;
	}


	public static void invokePrivate(Object instance, String methodName) throws Throwable {
		Method method = instance.getClass().getDeclaredMethod(methodName);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		handle.invoke(instance);
	}

	public static void invokePrivate(Object instance, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		Method method = instance.getClass().getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		Object[] args = new Object[params.length + 1];
		args[0] = instance;
		System.arraycopy(params, 0, args, 1, params.length);
		handle.invokeWithArguments(args);
	}

	public static <T> T invokePrivate(Object instance, String methodName, Class<T> returnType) throws Throwable {
		Method method = instance.getClass().getDeclaredMethod(methodName);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		Object rs = handle.invoke(instance);
		return (T) rs;
	}


	public static <T> T invokePrivate(Object instance, String methodName, Class<T> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		Method method = instance.getClass().getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		Object[] args = new Object[params.length + 1];
		args[0] = instance;
		System.arraycopy(params, 0, args, 1, params.length);
		Object rs = handle.invokeWithArguments(args);
		return (T) rs;
	}


	public static void invokeStatic(Class<?> clazz, String methodName) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class);
		MethodHandle handle = lookup.findStatic(clazz, methodName, methodType);
		handle.invoke();
	}

	public static void invokeStatic(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(void.class, parameterTypes);
		MethodHandle handle = lookup.findStatic(clazz, methodName, methodType);
		handle.invokeWithArguments(params);
	}

	public static <T> T invokeStatic(Class<?> clazz, String methodName, Class<T> returnType) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(returnType);
		MethodHandle handle = lookup.findStatic(clazz, methodName, methodType);
		Object rs = handle.invoke();
		return (T) rs;
	}

	public static <T> T invokeStatic(Class<?> clazz, String methodName, Class<T> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(returnType, parameterTypes);
		MethodHandle handle = lookup.findStatic(clazz, methodName, methodType);
		Object rs = handle.invokeWithArguments(params);
		return (T) rs;
	}

	public static void invokePrivateStatic(Class<?> clazz, String methodName) throws Throwable {
		Method method = clazz.getDeclaredMethod(methodName);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		handle.invoke();
	}

	public static void invokePrivateStatic(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		handle.invokeWithArguments(params);
	}

	public static <T> T invokePrivateStatic(Class<?> clazz, String methodName, Class<T> returnType) throws Throwable {
		Method method = clazz.getDeclaredMethod(methodName);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		Object rs = handle.invoke();
		return (T) rs;
	}

	public static <T> T invokePrivateStatic(Class<?> clazz, String methodName, Class<T> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflect(method);
		Object rs = handle.invokeWithArguments(params);
		return (T) rs;
	}


	public static <T> T getField(Object instance, String fieldName, Class<T> fieldType) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.findGetter(instance.getClass(), fieldName, fieldType);
		Object rs = handle.invoke(instance);
		return (T) rs;
	}

	public static <T> T getPrivateField(Object instance, String fieldName, Class<T> fieldType) throws Throwable {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflectGetter(field);
		Object rs = handle.invoke(instance);
		return (T) rs;
	}

	public static <T> void setField(Object instance, String fieldName, Class<T> fieldType, T value) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.findSetter(instance.getClass(), fieldName, fieldType);
		handle.invoke(instance, value);
	}

	public static <T> void setPrivateField(Object instance, String fieldName, Class<T> fieldType, T value) throws Throwable {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflectSetter(field);
		handle.invoke(instance, value);
	}

	public static <T> T getStaticField(Class<?> clazz, String fieldName, Class<T> fieldType) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.findStaticGetter(clazz, fieldName, fieldType);
		Object rs = handle.invoke();
		return (T) rs;
	}

	public static <T> T getPrivateStaticField(Class<?> clazz, String fieldName, Class<T> fieldType) throws Throwable {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflectGetter(field);
		Object rs = handle.invoke();
		return (T) rs;
	}

	public static <T> void setStaticField(Class<?> clazz, String fieldName, Class<T> fieldType, T value) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.findStaticSetter(clazz, fieldName, fieldType);
		handle.invoke(value);
	}

	public static <T> void setPrivateStaticField(Class<?> clazz, String fieldName, Class<T> fieldType, T value) throws Throwable {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.unreflectSetter(field);
		handle.invoke(value);
	}
}

package io.polaris.core.reflect;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

/**
 * @author Qt
 * @since Sep 09, 2025
 */
@ThreadSafe
@SuppressWarnings("unchecked")
public class ClassHandle<T> {
	private final Class<T> clazz;
	private MethodHandle defaultConstructor;
	private MethodHandle defaultPrivateConstructor;
	private volatile Map<ConstructorKey, MethodHandle> constructors;
	private volatile Map<ConstructorKey, MethodHandle> privateConstructors;
	private volatile Map<MethodKey, MethodHandle> methods;
	private volatile Map<MethodKey, MethodHandle> staticMethods;
	private volatile Map<MethodKey, MethodHandle> privateMethods;
	private volatile Map<MethodKey, MethodHandle> privateStaticMethods;
	private volatile Map<FieldKey, MethodHandle> fieldGetters;
	private volatile Map<FieldKey, MethodHandle> staticFieldGetters;
	private volatile Map<FieldKey, MethodHandle> privateFieldGetters;
	private volatile Map<FieldKey, MethodHandle> privateStaticFieldGetters;
	private volatile Map<FieldKey, MethodHandle> fieldSetters;
	private volatile Map<FieldKey, MethodHandle> staticFieldSetters;
	private volatile Map<FieldKey, MethodHandle> privateFieldSetters;
	private volatile Map<FieldKey, MethodHandle> privateStaticFieldSetters;

	public ClassHandle(Class<T> clazz) {
		this.clazz = clazz;
	}

	public T newInstance() throws Throwable {
		if (defaultConstructor == null) {
			defaultConstructor = Methods.findConstructor(clazz);
		}
		return (T) defaultConstructor.invoke();
	}

	public T newInstance(Class<?>[] parameterTypes, Object[] params) throws Throwable {
		if (constructors == null) {
			synchronized (this) {
				if (constructors == null) {
					constructors = new ConcurrentHashMap<>();
				}
			}
		}
		ConstructorKey constructorKey = new ConstructorKey(parameterTypes);
		MethodHandle handle = constructors.get(constructorKey);
		if (handle == null) {
			synchronized (this) {
				handle = constructors.get(constructorKey);
				if (handle == null) {
					handle = Methods.findConstructor(clazz, parameterTypes);
					constructors.put(constructorKey, handle);
				}
			}
		}
		return (T) handle.invokeWithArguments(params);
	}


	public T newPrivateInstance() throws Throwable {
		if (defaultPrivateConstructor == null) {
			defaultPrivateConstructor = Methods.findPrivateConstructor(clazz);
		}
		return (T) defaultPrivateConstructor.invoke();
	}

	public T newPrivateInstance(Class<?>[] parameterTypes, Object[] params) throws Throwable {
		if (privateConstructors == null) {
			synchronized (this) {
				if (privateConstructors == null) {
					privateConstructors = new ConcurrentHashMap<>();
				}
			}
		}
		ConstructorKey constructorKey = new ConstructorKey(parameterTypes);
		MethodHandle handle = privateConstructors.get(constructorKey);
		if (handle == null) {
			synchronized (this) {
				handle = privateConstructors.get(constructorKey);
				if (handle == null) {
					handle = Methods.findPrivateConstructor(clazz, parameterTypes);
					privateConstructors.put(constructorKey, handle);
				}
			}
		}
		return (T) handle.invokeWithArguments(params);
	}


	private Object[] asInvokeArguments(T instance, Object[] params) {
		if (params.length == 0) {
			return new Object[]{instance};
		}
		Object[] args = new Object[params.length + 1];
		args[0] = instance;
		System.arraycopy(params, 0, args, 1, params.length);
		return args;
	}

	public void invoke(T instance, String methodName) throws Throwable {
		MethodHandle handle = getMethod(methodName, void.class, new Class<?>[]{});
		handle.invoke(instance);
	}

	public void invoke(T instance, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = getMethod(methodName, void.class, parameterTypes);
		Object[] args = asInvokeArguments(instance, params);
		handle.invokeWithArguments(args);
	}

	public <R> R invoke(T instance, String methodName, Class<R> returnType) throws Throwable {
		MethodHandle handle = getMethod(methodName, returnType, new Class<?>[]{});
		return (R) handle.invoke(instance);
	}

	public <R> R invoke(T instance, String methodName, Class<R> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = getMethod(methodName, returnType, parameterTypes);
		Object[] args = asInvokeArguments(instance, params);
		return (R) handle.invokeWithArguments(args);
	}

	private MethodHandle getMethod(String methodName, Class<?> returnType, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		if (methods == null) {
			synchronized (this) {
				if (methods == null) {
					methods = new ConcurrentHashMap<>();
				}
			}
		}
		MethodKey methodKey = new MethodKey(methodName, returnType, parameterTypes);
		MethodHandle handle = methods.get(methodKey);
		if (handle == null) {
			synchronized (this) {
				handle = methods.get(methodKey);
				if (handle == null) {
					handle = Methods.findVirtual(clazz, methodName, returnType, parameterTypes);
					methods.put(methodKey, handle);
				}
			}
		}
		return handle;
	}


	public void invokeStatic(String methodName) throws Throwable {
		MethodHandle handle = getStaticMethod(methodName, void.class, new Class<?>[]{});
		handle.invoke();
	}

	public void invokeStatic(String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = getStaticMethod(methodName, void.class, parameterTypes);
		handle.invokeWithArguments(params);
	}

	public <R> R invokeStatic(String methodName, Class<R> returnType) throws Throwable {
		MethodHandle handle = getStaticMethod(methodName, returnType, new Class<?>[]{});
		return (R) handle.invoke();
	}

	public <R> R invokeStatic(String methodName, Class<R> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = getStaticMethod(methodName, returnType, parameterTypes);
		return (R) handle.invokeWithArguments(params);
	}

	private MethodHandle getStaticMethod(String methodName, Class<?> returnType, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		if (staticMethods == null) {
			synchronized (this) {
				if (staticMethods == null) {
					staticMethods = new ConcurrentHashMap<>();
				}
			}
		}
		MethodKey methodKey = new MethodKey(methodName, returnType, parameterTypes);
		MethodHandle handle = staticMethods.get(methodKey);
		if (handle == null) {
			synchronized (this) {
				handle = staticMethods.get(methodKey);
				if (handle == null) {
					handle = Methods.findStatic(clazz, methodName, returnType, parameterTypes);
					staticMethods.put(methodKey, handle);
				}
			}
		}
		return handle;
	}


	public void invokePrivate(T instance, String methodName) throws Throwable {
		MethodHandle handle = getPrivateMethod(methodName, void.class, new Class<?>[]{});
		handle.invoke(instance);
	}

	public void invokePrivate(T instance, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = getPrivateMethod(methodName, void.class, parameterTypes);
		Object[] args = asInvokeArguments(instance, params);
		handle.invokeWithArguments(args);
	}

	public <R> R invokePrivate(T instance, String methodName, Class<R> returnType) throws Throwable {
		MethodHandle handle = getPrivateMethod(methodName, returnType, new Class<?>[]{});
		return (R) handle.invoke(instance);
	}

	public <R> R invokePrivate(T instance, String methodName, Class<R> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = getPrivateMethod(methodName, returnType, parameterTypes);
		Object[] args = asInvokeArguments(instance, params);
		return (R) handle.invokeWithArguments(args);
	}

	private MethodHandle getPrivateMethod(String methodName, Class<?> returnType, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		if (privateMethods == null) {
			synchronized (this) {
				if (privateMethods == null) {
					privateMethods = new ConcurrentHashMap<>();
				}
			}
		}
		MethodKey methodKey = new MethodKey(methodName, returnType, parameterTypes);
		MethodHandle handle = privateMethods.get(methodKey);
		if (handle == null) {
			synchronized (this) {
				handle = privateMethods.get(methodKey);
				if (handle == null) {
					handle = Methods.findPrivate(clazz, methodName, returnType, parameterTypes);
					privateMethods.put(methodKey, handle);
				}
			}
		}
		return handle;
	}


	public void invokePrivateStatic(String methodName) throws Throwable {
		MethodHandle handle = getPrivateStaticMethod(methodName, void.class, new Class<?>[]{});
		handle.invoke();
	}

	public void invokePrivateStatic(String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = getPrivateStaticMethod(methodName, void.class, parameterTypes);
		handle.invokeWithArguments(params);
	}

	public <R> R invokePrivateStatic(String methodName, Class<R> returnType) throws Throwable {
		MethodHandle handle = getPrivateStaticMethod(methodName, returnType, new Class<?>[]{});
		return (R) handle.invoke();
	}

	public <R> R invokePrivateStatic(String methodName, Class<R> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandle handle = getPrivateStaticMethod(methodName, returnType, parameterTypes);
		return (R) handle.invokeWithArguments(params);
	}

	private MethodHandle getPrivateStaticMethod(String methodName, Class<?> returnType, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		if (privateStaticMethods == null) {
			synchronized (this) {
				if (privateStaticMethods == null) {
					privateStaticMethods = new ConcurrentHashMap<>();
				}
			}
		}
		MethodKey methodKey = new MethodKey(methodName, returnType, parameterTypes);
		MethodHandle handle = privateStaticMethods.get(methodKey);
		if (handle == null) {
			synchronized (this) {
				handle = privateStaticMethods.get(methodKey);
				if (handle == null) {
					handle = Methods.findPrivateStatic(clazz, methodName, returnType, parameterTypes);
					privateStaticMethods.put(methodKey, handle);
				}
			}
		}
		return handle;
	}


	public <R> R getField(T instance, String fieldName, Class<R> fieldType) throws Throwable {
		MethodHandle handle = getFieldGetter(fieldName, fieldType);
		return (R) handle.invoke(instance);
	}

	private MethodHandle getFieldGetter(String fieldName, Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException {
		if (fieldGetters == null) {
			synchronized (this) {
				if (fieldGetters == null) {
					fieldGetters = new ConcurrentHashMap<>();
				}
			}
		}
		FieldKey fieldKey = new FieldKey(fieldName, fieldType);
		MethodHandle handle = fieldGetters.get(fieldKey);
		if (handle == null) {
			synchronized (this) {
				handle = fieldGetters.get(fieldKey);
				if (handle == null) {
					handle = Methods.findGetter(clazz, fieldName, fieldType);
					fieldGetters.put(fieldKey, handle);
				}
			}
		}
		return handle;
	}

	public <R> R getStaticField(String fieldName, Class<R> fieldType) throws Throwable {
		MethodHandle handle = getStaticFieldGetter(fieldName, fieldType);
		return (R) handle.invoke();
	}

	private MethodHandle getStaticFieldGetter(String fieldName, Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException {
		if (staticFieldGetters == null) {
			synchronized (this) {
				if (staticFieldGetters == null) {
					staticFieldGetters = new ConcurrentHashMap<>();
				}
			}
		}
		FieldKey fieldKey = new FieldKey(fieldName, fieldType);
		MethodHandle handle = staticFieldGetters.get(fieldKey);
		if (handle == null) {
			synchronized (this) {
				handle = staticFieldGetters.get(fieldKey);
				if (handle == null) {
					handle = Methods.findStaticGetter(clazz, fieldName, fieldType);
					staticFieldGetters.put(fieldKey, handle);
				}
			}
		}
		return handle;
	}

	public <R> R getPrivateField(T instance, String fieldName, Class<R> fieldType) throws Throwable {
		MethodHandle handle = getPrivateFieldGetter(fieldName, fieldType);
		return (R) handle.invoke(instance);
	}

	private MethodHandle getPrivateFieldGetter(String fieldName, Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException {
		if (privateFieldGetters == null) {
			synchronized (this) {
				if (privateFieldGetters == null) {
					privateFieldGetters = new ConcurrentHashMap<>();
				}
			}
		}
		FieldKey fieldKey = new FieldKey(fieldName, fieldType);
		MethodHandle handle = privateFieldGetters.get(fieldKey);
		if (handle == null) {
			synchronized (this) {
				handle = privateFieldGetters.get(fieldKey);
				if (handle == null) {
					handle = Methods.findPrivateGetter(clazz, fieldName, fieldType);
					privateFieldGetters.put(fieldKey, handle);
				}
			}
		}
		return handle;
	}

	public <R> R getPrivateStaticField(String fieldName, Class<R> fieldType) throws Throwable {
		MethodHandle handle = getPrivateFieldGetter(fieldName, fieldType);
		return (R) handle.invoke();
	}

	private MethodHandle getPrivateStaticFieldGetter(String fieldName, Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException {
		if (privateStaticFieldGetters == null) {
			synchronized (this) {
				if (privateStaticFieldGetters == null) {
					privateStaticFieldGetters = new ConcurrentHashMap<>();
				}
			}
		}
		FieldKey fieldKey = new FieldKey(fieldName, fieldType);
		MethodHandle handle = privateStaticFieldGetters.get(fieldKey);
		if (handle == null) {
			synchronized (this) {
				handle = privateStaticFieldGetters.get(fieldKey);
				if (handle == null) {
					handle = Methods.findPrivateStaticGetter(clazz, fieldName, fieldType);
					privateStaticFieldGetters.put(fieldKey, handle);
				}
			}
		}
		return handle;
	}


	public <R> void setField(T instance, String fieldName, Class<R> fieldType, R value) throws Throwable {
		MethodHandle handle = getFieldSetter(fieldName, fieldType);
		handle.invoke(instance, value);
	}

	private MethodHandle getFieldSetter(String fieldName, Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException {
		if (fieldSetters == null) {
			synchronized (this) {
				if (fieldSetters == null) {
					fieldSetters = new ConcurrentHashMap<>();
				}
			}
		}
		FieldKey fieldKey = new FieldKey(fieldName, fieldType);
		MethodHandle handle = fieldSetters.get(fieldKey);
		if (handle == null) {
			synchronized (this) {
				handle = fieldSetters.get(fieldKey);
				if (handle == null) {
					handle = Methods.findSetter(clazz, fieldName, fieldType);
					fieldSetters.put(fieldKey, handle);
				}
			}
		}
		return handle;
	}


	public <R> void setStaticField(String fieldName, Class<R> fieldType, R value) throws Throwable {
		MethodHandle handle = getStaticFieldSetter(fieldName, fieldType);
		handle.invoke(value);
	}

	private MethodHandle getStaticFieldSetter(String fieldName, Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException {
		if (staticFieldSetters == null) {
			synchronized (this) {
				if (staticFieldSetters == null) {
					staticFieldSetters = new ConcurrentHashMap<>();
				}
			}
		}
		FieldKey fieldKey = new FieldKey(fieldName, fieldType);
		MethodHandle handle = staticFieldSetters.get(fieldKey);
		if (handle == null) {
			synchronized (this) {
				handle = staticFieldSetters.get(fieldKey);
				if (handle == null) {
					handle = Methods.findStaticSetter(clazz, fieldName, fieldType);
					staticFieldSetters.put(fieldKey, handle);
				}
			}
		}
		return handle;
	}

	public <R> void setPrivateField(T instance, String fieldName, Class<R> fieldType, R value) throws Throwable {
		MethodHandle handle = getPrivateFieldSetter(fieldName, fieldType);
		handle.invoke(instance, value);
	}

	private MethodHandle getPrivateFieldSetter(String fieldName, Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException {
		if (privateFieldSetters == null) {
			synchronized (this) {
				if (privateFieldSetters == null) {
					privateFieldSetters = new ConcurrentHashMap<>();
				}
			}
		}
		FieldKey fieldKey = new FieldKey(fieldName, fieldType);
		MethodHandle handle = privateFieldSetters.get(fieldKey);
		if (handle == null) {
			synchronized (this) {
				handle = privateFieldSetters.get(fieldKey);
				if (handle == null) {
					handle = Methods.findPrivateSetter(clazz, fieldName, fieldType);
					privateFieldSetters.put(fieldKey, handle);
				}
			}
		}
		return handle;
	}

	public <R> void setPrivateStaticField(String fieldName, Class<R> fieldType, R value) throws Throwable {
		MethodHandle handle = getPrivateStaticFieldSetter(fieldName, fieldType);
		handle.invoke(value);
	}

	private MethodHandle getPrivateStaticFieldSetter(String fieldName, Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException {
		if (privateStaticFieldSetters == null) {
			synchronized (this) {
				if (privateStaticFieldSetters == null) {
					privateStaticFieldSetters = new ConcurrentHashMap<>();
				}
			}
		}
		FieldKey fieldKey = new FieldKey(fieldName, fieldType);
		MethodHandle handle = privateStaticFieldSetters.get(fieldKey);
		if (handle == null) {
			synchronized (this) {
				handle = privateStaticFieldSetters.get(fieldKey);
				if (handle == null) {
					handle = Methods.findPrivateStaticSetter(clazz, fieldName, fieldType);
					privateStaticFieldSetters.put(fieldKey, handle);
				}
			}
		}
		return handle;
	}


}

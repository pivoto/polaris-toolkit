package io.polaris.core.reflect;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

/**
 * @author Qt
 * @since Oct 13, 2025
 */
@ThreadSafe
@SuppressWarnings("unchecked")
public class SmartClassHandle<T> {

	static class MethodHandleNode {
		final MethodHandle handle;
		final boolean isStatic;

		public MethodHandleNode(MethodHandle handle, boolean isStatic) {
			this.handle = handle;
			this.isStatic = isStatic;
		}
	}

	private final Class<T> clazz;
	private MethodHandle defaultConstructor;
	private volatile Map<ConstructorKey, MethodHandle> constructors;
	private volatile Map<MethodKey, MethodHandleNode> methods;
	private volatile Map<FieldKey, MethodHandleNode> fieldGetters;
	private volatile Map<FieldKey, MethodHandleNode> fieldSetters;

	public SmartClassHandle(Class<T> clazz) {
		this.clazz = clazz;
	}

	public T newInstance() throws Throwable {
		if (defaultConstructor == null) {
			MethodHandle constructor = null;
			try {
				constructor = Methods.findConstructor(clazz);
			} catch (NoSuchMethodException | IllegalAccessException e) {
				constructor = Methods.findPrivateConstructor(clazz);
			}
			defaultConstructor = constructor;
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
					try {
						handle = Methods.findConstructor(clazz, parameterTypes);
					} catch (NoSuchMethodException | IllegalAccessException e) {
						handle = Methods.findPrivateConstructor(clazz, parameterTypes);
					}
					constructors.put(constructorKey, handle);
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


	private MethodHandleNode getMethod(String methodName, Class<?> returnType, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
		if (methods == null) {
			synchronized (this) {
				if (methods == null) {
					methods = new ConcurrentHashMap<>();
				}
			}
		}
		MethodKey methodKey = new MethodKey(methodName, returnType, parameterTypes);
		MethodHandleNode node = methods.get(methodKey);
		if (node == null) {
			synchronized (this) {
				node = methods.get(methodKey);
				if (node == null) {
					boolean isStatic = false;
					MethodHandle handle = null;
					NoSuchMethodException e1 = null;
					IllegalAccessException e2 = null;
					try {
						handle = Methods.findVirtual(clazz, methodName, returnType, parameterTypes);
					} catch (NoSuchMethodException e) {
						e1 = e;
					} catch (IllegalAccessException e) {
						e2 = e;
					}
					if (handle == null) {
						try {
							handle = Methods.findStatic(clazz, methodName, returnType, parameterTypes);
							isStatic = true;
						} catch (NoSuchMethodException | IllegalAccessException ignored) {
						}
					}
					if (handle == null) {
						try {
							handle = Methods.findPrivate(clazz, methodName, returnType, parameterTypes);
							isStatic = false;
						} catch (NoSuchMethodException | IllegalAccessException ignored) {
						}
					}
					if (handle == null) {
						try {
							handle = Methods.findPrivateStatic(clazz, methodName, returnType, parameterTypes);
							isStatic = true;
						} catch (NoSuchMethodException | IllegalAccessException ignored) {
						}
					}
					if (handle == null) {
						if (e1 != null) {
							throw e1;
						}
						if (e2 != null) {
							throw e2;
						}
						throw new NoSuchMethodException(methodName);
					}
					node = new MethodHandleNode(handle, isStatic);
					methods.put(methodKey, node);
				}
			}
		}
		return node;
	}


	public void invoke(T instance, String methodName) throws Throwable {
		MethodHandleNode node = getMethod(methodName, void.class, new Class<?>[]{});
		MethodHandle handle = node.handle;
		if (node.isStatic) {
			handle.invoke();
		} else {
			handle.invoke(instance);
		}
	}

	public void invoke(T instance, String methodName, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandleNode node = getMethod(methodName, void.class, parameterTypes);
		MethodHandle handle = node.handle;
		if (node.isStatic) {
			handle.invokeWithArguments(params);
		} else {
			Object[] args = asInvokeArguments(instance, params);
			handle.invokeWithArguments(args);
		}
	}

	public <R> R invoke(T instance, String methodName, Class<R> returnType) throws Throwable {
		MethodHandleNode node = getMethod(methodName, returnType, new Class<?>[]{});
		MethodHandle handle = node.handle;
		if (node.isStatic) {
			return (R) handle.invoke();
		} else {
			return (R) handle.invoke(instance);
		}
	}

	public <R> R invoke(T instance, String methodName, Class<R> returnType, Class<?>[] parameterTypes, Object[] params) throws Throwable {
		MethodHandleNode node = getMethod(methodName, returnType, parameterTypes);
		MethodHandle handle = node.handle;
		if (node.isStatic) {
			return (R) handle.invokeWithArguments(params);
		} else {
			Object[] args = asInvokeArguments(instance, params);
			return (R) handle.invokeWithArguments(args);
		}
	}

	private MethodHandleNode getFieldGetter(String fieldName, Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException {
		if (fieldGetters == null) {
			synchronized (this) {
				if (fieldGetters == null) {
					fieldGetters = new ConcurrentHashMap<>();
				}
			}
		}
		FieldKey fieldKey = new FieldKey(fieldName, fieldType);
		MethodHandleNode node = fieldGetters.get(fieldKey);
		if (node == null) {
			synchronized (this) {
				node = fieldGetters.get(fieldKey);
				if (node == null) {
					boolean isStatic = false;
					MethodHandle handle = null;
					NoSuchFieldException e1 = null;
					IllegalAccessException e2 = null;
					try {
						handle = Methods.findGetter(clazz, fieldName, fieldType);
					} catch (NoSuchFieldException e) {
						e1 = e;
					} catch (IllegalAccessException e) {
						e2 = e;
					}
					if (handle == null) {
						try {
							handle = Methods.findStaticGetter(clazz, fieldName, fieldType);
							isStatic = true;
						} catch (NoSuchFieldException e) {
							e1 = e;
						} catch (IllegalAccessException e) {
							e2 = e;
						}
					}
					if (handle == null) {
						try {
							handle = Methods.findPrivateGetter(clazz, fieldName, fieldType);
							isStatic = false;
						} catch (NoSuchFieldException e) {
							e1 = e;
						} catch (IllegalAccessException e) {
							e2 = e;
						}
					}
					if (handle == null) {
						try {
							handle = Methods.findPrivateStaticGetter(clazz, fieldName, fieldType);
							isStatic = true;
						} catch (NoSuchFieldException e) {
							e1 = e;
						} catch (IllegalAccessException e) {
							e2 = e;
						}
					}
					if (handle == null) {
						if (e1 != null) {
							throw e1;
						}
						if (e2 != null) {
							throw e2;
						}
						throw new NoSuchFieldException(fieldName);
					}
					node = new MethodHandleNode(handle, isStatic);
					fieldGetters.put(fieldKey, node);
				}
			}
		}
		return node;
	}


	public <R> R getField(T instance, String fieldName, Class<R> fieldType) throws Throwable {
		MethodHandleNode node = getFieldGetter(fieldName, fieldType);
		MethodHandle handle = node.handle;
		if (node.isStatic) {
			return (R) handle.invoke();
		} else {
			return (R) handle.invoke(instance);
		}
	}

	private MethodHandleNode getFieldSetter(String fieldName, Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException {
		if (fieldSetters == null) {
			synchronized (this) {
				if (fieldSetters == null) {
					fieldSetters = new ConcurrentHashMap<>();
				}
			}
		}
		FieldKey fieldKey = new FieldKey(fieldName, fieldType);
		MethodHandleNode node = fieldSetters.get(fieldKey);
		if (node == null) {
			synchronized (this) {
				node = fieldSetters.get(fieldKey);
				if (node == null) {
					boolean isStatic = false;
					MethodHandle handle = null;
					NoSuchFieldException e1 = null;
					IllegalAccessException e2 = null;
					try {
						handle = Methods.findSetter(clazz, fieldName, fieldType);
					} catch (NoSuchFieldException e) {
						e1 = e;
					} catch (IllegalAccessException e) {
						e2 = e;
					}
					if (handle == null) {
						try {
							handle = Methods.findStaticSetter(clazz, fieldName, fieldType);
							isStatic = true;
						} catch (NoSuchFieldException e) {
							e1 = e;
						} catch (IllegalAccessException e) {
							e2 = e;
						}
					}
					if (handle == null) {
						try {
							handle = Methods.findPrivateSetter(clazz, fieldName, fieldType);
							isStatic = false;
						} catch (NoSuchFieldException e) {
							e1 = e;
						} catch (IllegalAccessException e) {
							e2 = e;
						}
					}
					if (handle == null) {
						try {
							handle = Methods.findPrivateStaticSetter(clazz, fieldName, fieldType);
							isStatic = true;
						} catch (NoSuchFieldException e) {
							e1 = e;
						} catch (IllegalAccessException e) {
							e2 = e;
						}
					}
					if (handle == null) {
						if (e1 != null) {
							throw e1;
						}
						if (e2 != null) {
							throw e2;
						}
						throw new NoSuchFieldException(fieldName);
					}
					node = new MethodHandleNode(handle, isStatic);
					fieldSetters.put(fieldKey, node);
				}
			}
		}
		return node;
	}

	public <R> void setField(T instance, String fieldName, Class<R> fieldType, R value) throws Throwable {
		MethodHandleNode node = getFieldSetter(fieldName, fieldType);
		MethodHandle handle = node.handle;
		if (node.isStatic) {
			handle.invoke(value);
		} else {
			handle.invoke(instance, value);
		}
	}
}

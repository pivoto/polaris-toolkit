package io.polaris.core.reflect;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class Reflects {

	public static Object getFieldValue(Object o, String key) {
		try {
			Field f = null;
			Class clazz = o.getClass();
			while (f == null) {
				if (clazz.equals(Object.class)) {
					break;
				}
				try {
					f = clazz.getDeclaredField(key);
					break;
				} catch (Exception e) {
					clazz = clazz.getSuperclass();
				}
			}
			f.setAccessible(true);
			return f.get(o);
		} catch (Exception e) {
			throw new java.lang.UnsupportedOperationException(e);
		}
	}

	/** 获取指定类型的继承自泛型类声明的方法的返回值实参 */
	public static Class findMethodGenericReturnType(Method method, Class targetType) {
		Type genericReturnType = method.getGenericReturnType();
		if (genericReturnType instanceof TypeVariable) {
			GenericDeclaration genericDeclaration = ((TypeVariable) genericReturnType).getGenericDeclaration();
			TypeVariable<?>[] typeParameters = genericDeclaration.getTypeParameters();
			int idx = -1;
			for (int i = 0; i < typeParameters.length; i++) {
				TypeVariable<?> typeParameter = typeParameters[i];
				if (typeParameter == genericReturnType) {
					idx = i;
					break;
				}
			}
			if (idx >= 0) {
				return findParameterizedType(method.getDeclaringClass(), targetType, idx);
			}
		}
		return null;
	}

	/**
	 * 得到指定类型的指定位置的泛型实参
	 *
	 * @param parameterized 泛型基类
	 * @param obj           目标类
	 * @param index         位置
	 * @return
	 */
	public static Class findParameterizedType(Class parameterized, Object obj, int index) {
		return findParameterizedType(parameterized, obj.getClass(), index);
	}

	/**
	 * 得到指定类型的指定位置的泛型实参
	 *
	 * @param parameterized 泛型基类
	 * @param targetClass   目标类
	 * @param index         位置
	 * @return
	 */
	public static Class findParameterizedType(Class parameterized, Class targetClass, int index) {
		if (parameterized == targetClass || !parameterized.isAssignableFrom(targetClass)) {
			return null;
		}
		TypeVariable<? extends Class>[] typeParameters = parameterized.getTypeParameters();
		if (typeParameters.length <= index) {
			return null;
		}

		Deque<ParameterizedType> q = findParameterizedTypes(parameterized, targetClass);

		int i = index;
		for (ParameterizedType t = q.pollLast(); t != null; t = q.pollLast()) {
			Type[] actualTypeArguments = t.getActualTypeArguments();
			if (actualTypeArguments[i] instanceof Class) {
				return (Class) actualTypeArguments[i];
			} else if (actualTypeArguments[i] instanceof WildcardType) {
				return Object.class;
			} else if (actualTypeArguments[i] instanceof ParameterizedType) {
				Type rawType = ((ParameterizedType) actualTypeArguments[i]).getRawType();
				if (rawType instanceof Class) {
					return (Class) rawType;
				} else {
					return Object.class;
				}
			} else if (actualTypeArguments[i] instanceof TypeVariable) {
				for (int j = 0; j < i; j++) {
					if (actualTypeArguments[j] instanceof Class) {
						i--;
					}
				}
			}
		}
		return null;
	}


	static Deque<ParameterizedType> findParameterizedTypes(Class parameterized, Class targetClass) {
		Deque<ParameterizedType> q = new ArrayDeque<>();
		// region search
		Class that = targetClass;
		search:
		while (true) {
			//superclass
			Type genericSuperclass = that.getGenericSuperclass();
			if (genericSuperclass != null) {
				if (parameterized == genericSuperclass) {
					break search;
				}
				if (genericSuperclass instanceof ParameterizedType) {
					Type rawType = ((ParameterizedType) genericSuperclass).getRawType();
					if (parameterized == rawType) {
						q.offerLast((ParameterizedType) genericSuperclass);
						break search;
					} else if (rawType instanceof Class && parameterized.isAssignableFrom((Class) rawType)) {
						that = (Class) rawType;
						q.offerLast((ParameterizedType) genericSuperclass);
						continue search;
					}
				} else if (genericSuperclass instanceof Class && parameterized.isAssignableFrom((Class) genericSuperclass)) {
					that = (Class) genericSuperclass;
					continue search;
				}
			}
			//interfaces
			Type[] genericInterfaces = that.getGenericInterfaces();
			if (genericInterfaces != null && genericInterfaces.length > 0) {
				for (Type genericInterface : genericInterfaces) {
					if (parameterized == genericInterface) {
						break search;
					}
					if (genericInterface instanceof ParameterizedType) {
						Type rawType = ((ParameterizedType) genericInterface).getRawType();
						if (parameterized == rawType) {
							q.offerLast((ParameterizedType) genericInterface);
							break search;
						} else if (rawType instanceof Class && parameterized.isAssignableFrom((Class) rawType)) {
							that = (Class) rawType;
							q.offerLast((ParameterizedType) genericInterface);
							continue search;
						}
					} else if (genericInterface instanceof Class && parameterized.isAssignableFrom((Class) genericInterface)) {
						that = (Class) genericInterface;
						continue search;
					}
				}
			}
			// never or error
			break search;
		}
		// endregion
		return q;
	}


	/**
	 * 得到指定类型的最近的泛型信息中指定位置的实参
	 */
	public static Class findParameterizedType(Class clazz, int index) {
		Class[] actualTypeArguments = findParameterizedTypes(clazz);
		if (actualTypeArguments == null || actualTypeArguments.length == 0) {
			return null;
		}
		return actualTypeArguments[index];
	}

	public static Class firstParameterizedType(Class clazz) {
		return findParameterizedType(clazz, 0);
	}

	/** 获取类型的最近的泛型参数 */
	public static Class[] findParameterizedTypes(Class clazz) {
		Deque<ParameterizedType> parameterizedTypes = findAllParameterizedTypes(clazz);
		if (parameterizedTypes.isEmpty()) {
			return null;
		}
		ParameterizedType parameterizedType = parameterizedTypes.peekFirst();
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

		Class[] types = new Class[actualTypeArguments.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = (Class) actualTypeArguments[i];
		}
		return types;
	}


	/**
	 * 获取类型的最近的所有泛型信息
	 */
	static Deque<ParameterizedType> findAllParameterizedTypes(Class clazz) {
		Deque<ParameterizedType> rs = new ArrayDeque<>();
		Deque<Type> q = new ArrayDeque<>();
		q.offerLast(clazz);
		while (!q.isEmpty()) {
			Type type = q.pollFirst();
			if (type instanceof ParameterizedType) {
				rs.offerLast((ParameterizedType) type);
			} else if (type instanceof Class) {
				if (!((Class) type).isInterface()) {
					Type genericSuperclass = ((Class) type).getGenericSuperclass();
					if (genericSuperclass != null && genericSuperclass != Object.class) {
						q.offerLast(genericSuperclass);
					}
				}
				Type[] genericInterfaces = ((Class) type).getGenericInterfaces();
				for (Type genericInterface : genericInterfaces) {
					q.offerLast(genericInterface);
				}
			}
		}
		return rs;
	}

	private static String toGetterOrSetterName(String name) {
		if (name.startsWith("get") || name.startsWith("set")) {
			name = name.substring(3);
		} else if (name.startsWith("is")) {
			name = name.substring(2);
		}
		return Introspector.decapitalize(name);
	}


	public static <T> String getPropertyName(SerializableSupplier<T> getter) {
		return toGetterOrSetterName(getter.method().getName());
	}

	public static <T> String getPropertyName(SerializableConsumer<T> setter) {
		return toGetterOrSetterName(setter.method().getName());
	}

	public static <T, R> String getPropertyName(SerializableFunction<T, R> getter) {
		return toGetterOrSetterName(getter.method().getName());
	}

	public static interface SerializableFunction<T, R> extends Function<T, R>, Serializable, MethodReferenceReflection {
	}

	public static interface SerializableSupplier<T> extends Supplier<T>, Serializable, MethodReferenceReflection {
	}

	public static interface SerializableConsumer<T> extends Consumer<T>, Serializable, MethodReferenceReflection {
	}

	public static interface MethodReferenceReflection extends Serializable {
		default SerializedLambda serialized() {
			try {
				Method replaceMethod = getClass().getDeclaredMethod("writeReplace");
				replaceMethod.setAccessible(true);
				return (SerializedLambda) replaceMethod.invoke(this);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		default Class getContainingClass() {
			try {
				String className = serialized().getImplClass().replaceAll("/", ".");
				return Class.forName(className);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		default Method method() {
			SerializedLambda lambda = serialized();
			Class containingClass = getContainingClass();
			return Arrays.asList(containingClass.getDeclaredMethods())
				.stream()
				//TODO check parameter types to deal with overloads
				.filter(method -> Objects.equals(method.getName(), lambda.getImplMethodName()))
				.findFirst()
				.orElseThrow(RuntimeException::new);
		}

	}

}

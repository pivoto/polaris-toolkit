package io.polaris.core.lang;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author Qt
 * @since 1.8
 */
public class Types {
	private static final Map<Class, Class> primitiveWrapperTypes = new HashMap<>();
	private static final Map<Class, Class> wrapperPrimitiveTypes = new HashMap<>();
	private static final Map<String, Class> primitiveNameTypes = new HashMap<>();

	static {
		primitiveWrapperTypes.put(Integer.TYPE, Integer.class);
		primitiveWrapperTypes.put(Byte.TYPE, Byte.class);
		primitiveWrapperTypes.put(Short.TYPE, Short.class);
		primitiveWrapperTypes.put(Long.TYPE, Long.class);
		primitiveWrapperTypes.put(Character.TYPE, Character.class);
		primitiveWrapperTypes.put(Boolean.TYPE, Boolean.class);
		primitiveWrapperTypes.put(Float.TYPE, Float.class);
		primitiveWrapperTypes.put(Double.TYPE, Double.class);
		// reverse
		primitiveWrapperTypes.forEach((k, v) -> {
			wrapperPrimitiveTypes.put(v, k);
			primitiveNameTypes.put(k.getName(), k);
		});
	}

	public static Class getPrimitiveClassByName(String type) {
		return primitiveNameTypes.get(type);
	}

	public static Class getPrimitiveClassByWrapper(Class type) {
		return type.isPrimitive() ? type : wrapperPrimitiveTypes.get(type);
	}

	/**
	 * 获取包装类型
	 */
	public static Class getWrapperClass(Class type) {
		if (!type.isPrimitive()) {
			return type;
		}
		return primitiveWrapperTypes.getOrDefault(type, type);
	}

	@Nullable
	public static Type getTypeArgument(Type type) {
		return getTypeArgument(type, 0);
	}

	@Nullable
	public static Type getTypeArgument(Type type, int index) {
		final Type[] typeArguments = getTypeArguments(type);
		if (null != typeArguments && typeArguments.length > index) {
			return typeArguments[index];
		}
		return null;
	}

	@Nullable
	public static Type[] getTypeArguments(Type type) {
		if (null == type) {
			return null;
		}

		final ParameterizedType parameterizedType = toParameterizedType(type);
		return (null == parameterizedType) ? null : parameterizedType.getActualTypeArguments();
	}

	@Nullable
	public static ParameterizedType toParameterizedType(Type type) {
		ParameterizedType result = null;
		if (type instanceof ParameterizedType) {
			result = (ParameterizedType) type;
		} else if (type instanceof Class) {
			final Class<?> clazz = (Class<?>) type;
			Type genericSuper = clazz.getGenericSuperclass();
			if (null == genericSuper || Object.class.equals(genericSuper)) {
				// 如果类没有父类，而是实现一些定义好的泛型接口，则取接口的Type
				final Type[] genericInterfaces = clazz.getGenericInterfaces();
				if (genericInterfaces.length > 0) {
					// 默认取第一个实现接口的泛型Type
					genericSuper = genericInterfaces[0];
				}
			}
			result = toParameterizedType(genericSuper);
		}
		return result;
	}

	/**
	 * 获取泛型变量和泛型实际类型的对应关系Map
	 */
	public static Map<TypeVariable, Type> getTypeVariableMap(Type type) {
		Map<TypeVariable, Type> typeMap = new HashMap<>();
		while (null != type) {
			ParameterizedType parameterizedType = Types.toParameterizedType(type);
			if (null == parameterizedType) {
				break;
			}
			final Type[] typeArguments = parameterizedType.getActualTypeArguments();
			final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
			final TypeVariable[] typeParameters = rawType.getTypeParameters();

			Type value;
			for (int i = 0; i < typeParameters.length; i++) {
				value = typeArguments[i];
				// 跳过泛型变量对应泛型变量的情况
				if (!(value instanceof TypeVariable)) {
					typeMap.put(typeParameters[i], value);
				}
			}
			type = rawType;
		}
		return typeMap;
	}

	/**
	 * 获得泛型变量对应的泛型实际类型，如果此变量没有对应的实际类型，返回null
	 */
	public static Type getActualType(Type type, TypeVariable<?> typeVariable) {
		Map<TypeVariable, Type> map = getTypeVariableMap(type);
		Type rs = map.get(typeVariable);
		while (rs instanceof TypeVariable) {
			rs = map.get(rs);
		}
		return rs;
	}

	public static boolean isUnknown(Type type) {
		return type == null || type instanceof TypeVariable;
	}


	/**
	 * 获得Type对应的原始类，如果无法获取原始类，返回null
	 */
	public static Class<?> getClass(Type type) {
		if (type == null) {
			return null;
		}
		if (type instanceof Class) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType) type).getRawType();
			if (rawType instanceof Class) {
				return (Class) rawType;
			} else {
				return Object.class;
			}
		} else if (type instanceof TypeVariable) {
			return getClass(((TypeVariable<?>) type).getBounds()[0]);
		} else if (type instanceof WildcardType) {
			final Type[] upperBounds = ((WildcardType) type).getUpperBounds();
			if (upperBounds.length == 1) {
				return getClass(upperBounds[0]);
			}
			return Object.class;
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			Type componentType = genericArrayType.getGenericComponentType();
			Class<?> componentClass = getClass(componentType);
			return getArrayClass(componentClass);
		}
		return Object.class;
	}

	public static Class<?> getArrayClass(Class componentClass) {
		if (componentClass == int.class) {
			return int[].class;
		}
		if (componentClass == byte.class) {
			return byte[].class;
		}
		if (componentClass == short.class) {
			return short[].class;
		}
		if (componentClass == long.class) {
			return long[].class;
		}
		if (componentClass == String.class) {
			return String[].class;
		}
		if (componentClass == Object.class) {
			return Object[].class;
		}
		return Array.newInstance(componentClass, 1).getClass();
	}

	/**
	 * 获取字段对应的Type类型， 优先获取GenericType，获取不到则获取Type
	 */
	public static Type getType(Field field) {
		if (null == field) {
			return null;
		}
		return field.getGenericType();
	}

	public static Object getDefaultValue(Class<?> clazz) {
		if (long.class == clazz) {
			return 0L;
		} else if (int.class == clazz) {
			return 0;
		} else if (short.class == clazz) {
			return (short) 0;
		} else if (char.class == clazz) {
			return (char) 0;
		} else if (byte.class == clazz) {
			return (byte) 0;
		} else if (double.class == clazz) {
			return 0D;
		} else if (float.class == clazz) {
			return 0f;
		} else if (boolean.class == clazz) {
			return false;
		} else if (clazz == Optional.class) {
			return Optional.empty();
		} else if (clazz == OptionalInt.class) {
			return OptionalInt.empty();
		} else if (clazz == OptionalLong.class) {
			return OptionalLong.empty();
		} else if (clazz == OptionalDouble.class) {
			return OptionalDouble.empty();
		}
		return null;
	}

	public static Object[] getDefaultValues(Class<?>... classes) {
		final Object[] values = new Object[classes.length];
		for (int i = 0; i < classes.length; i++) {
			values[i] = getDefaultValue(classes[i]);
		}
		return values;
	}


	public static boolean isFunction(Class type) {
		if (type.isInterface()) {
			String typeName = type.getName();
			if (typeName.startsWith("java.util.function.")) {
				return true;
			}

			if (type.isAnnotationPresent(FunctionalInterface.class)) {
				return true;
			}
		}

		return false;
	}
}

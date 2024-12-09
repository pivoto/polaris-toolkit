package io.polaris.core.lang;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
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

	public static boolean isPrimitive(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return clazz.isPrimitive();
	}

	public static boolean isPrimitiveWrapper(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return wrapperPrimitiveTypes.containsKey(clazz);
	}

	/**
	 * 获取包装类型
	 */
	public static Class<?> getWrapperClass(Class<?> type) {
		if (!type.isPrimitive()) {
			return type;
		}
		return primitiveWrapperTypes.getOrDefault(type, type);
	}

	public Type getArrayComponentType(Type type) {
		if (type instanceof GenericArrayType) {
			return ((GenericArrayType) type).getGenericComponentType();
		} else {
			return Types.getClass(type).getComponentType();
		}
	}

	@Nullable
	public static Type getTypeArgument(Type type) {
		return getTypeArgument(type, 0);
	}

	@Nullable
	public static Type getTypeArgument(Type type, int index) {
		final Type[] typeArguments = getTypeArguments(type);
		if (typeArguments.length > index) {
			return typeArguments[index];
		}
		return null;
	}

	@Nonnull
	public static Type[] getTypeArguments(@Nonnull Type type) {
		final ParameterizedType parameterizedType = toParameterizedType(type);
		return (null == parameterizedType) ? new Type[0] : parameterizedType.getActualTypeArguments();
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

	@SuppressWarnings("rawtypes")
	static Map<TypeVariable, Type> _getTypeVariableMap(Type type) {
		Map<TypeVariable, Type> typeMap = new HashMap<>();
		while (null != type) {
			ParameterizedType parameterizedType = Types.toParameterizedType(type);
			if (null == parameterizedType) {
				break;
			}
			final Type[] typeArguments = parameterizedType.getActualTypeArguments();
			final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
			final TypeVariable[] typeParameters = rawType.getTypeParameters();

			for (int i = 0; i < typeParameters.length; i++) {
				Type value = typeArguments[i];
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
	 * 获取泛型变量和泛型实际类型的对应关系Map
	 */
	@SuppressWarnings("rawtypes")
	public static Map<TypeVariable<?>, Type> getTypeVariableMap(Type type) {
		Map<TypeVariable<?>, Type> typeMap = new HashMap<>();
		fetchTypeVariableMap(typeMap, type);
		return typeMap;
	}

	@SuppressWarnings("rawtypes")
	private static void fetchTypeVariableMap(Map<TypeVariable<?>, Type> typeMap, Type type) {
		if (type instanceof ParameterizedType) {
			Map<String, Type> nameMap = new HashMap<>();
			final Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
			{
				Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();
				final TypeVariable[] variables = rawType.getTypeParameters();
				for (int i = 0; i < variables.length; i++) {
					Type value = actualTypes[i];
					// 跳过泛型变量对应泛型变量的情况
					if (!(value instanceof TypeVariable)) {
						typeMap.put(variables[i], value);
						nameMap.putIfAbsent(variables[i].getName(), value);
					} else {
						Type existed = typeMap.get(variables[i]);
						if (existed != null) {
							nameMap.putIfAbsent(variables[i].getName(), existed);
						}
					}
				}
			}
			// 父类泛型处理
			{
				Type genericSuper = rawType.getGenericSuperclass();
				fetchTypeVariableMap(typeMap, nameMap, genericSuper);
			}
			// 接口泛型处理
			{
				Type[] genericInterfaces = rawType.getGenericInterfaces();
				for (Type genericInterface : genericInterfaces) {
					fetchTypeVariableMap(typeMap, nameMap, genericInterface);
				}
			}
		} else if (type instanceof Class) {
			// 父类与接口泛型处理
			fetchTypeVariableMap(typeMap, ((Class<?>) type).getGenericSuperclass());
			for (Type genericInterface : ((Class<?>) type).getGenericInterfaces()) {
				fetchTypeVariableMap(typeMap, genericInterface);
			}
		}
	}

	private static void fetchTypeVariableMap(Map<TypeVariable<?>, Type> typeMap, Map<String, Type> nameMap, Type genericInterface) {
		if (genericInterface instanceof ParameterizedType) {
			Class<?> superRawType = (Class<?>) ((ParameterizedType) genericInterface).getRawType();
			Type[] superActualTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
			TypeVariable[] superVariables = superRawType.getTypeParameters();
			for (int i = 0; i < superVariables.length; i++) {
				Type value = superActualTypes[i];
				if (!(value instanceof TypeVariable)) {
					typeMap.put(superVariables[i], value);
					nameMap.putIfAbsent(superVariables[i].getName(), value);
				} else {
					Type existed = typeMap.get(superVariables[i]);
					if (existed != null) {
						nameMap.putIfAbsent(superVariables[i].getName(), existed);
					} else {
						value = nameMap.get(superVariables[i].getName());
						if (value != null) {
							typeMap.put(superVariables[i], value);
						}
					}
				}
			}
		}
	}


	/**
	 * 获得泛型变量对应的泛型实际类型，如果此变量没有对应的实际类型，返回null
	 */
	public static Type getActualType(Type type, TypeVariable<?> typeVariable) {
		Map<TypeVariable<?>, Type> map = getTypeVariableMap(type);
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
	 * 获得Type对应的原始类，如果无法获取原始类，返回Object.class
	 */
	@Nonnull
	public static Class<?> getClass(@Nonnull Type type) {
		// 直接从 JavaType 获取
		if (type instanceof JavaType) {
			return ((JavaType<?>) type).getRawClass();
		}
		if (type instanceof Class) {
			return (Class<?>) type;
		}
		if (type instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType) type).getRawType();
			if (rawType instanceof Class) {
				return (Class) rawType;
			} else {
				return Object.class;
			}
		}
		if (type instanceof TypeVariable) {
			return getClass(((TypeVariable<?>) type).getBounds()[0]);
		}
		if (type instanceof WildcardType) {
			final Type[] upperBounds = ((WildcardType) type).getUpperBounds();
			if (upperBounds.length == 1) {
				return getClass(upperBounds[0]);
			}
			return Object.class;
		}
		if (type instanceof GenericArrayType) {
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
		if (componentClass == long.class) {
			return long[].class;
		}
		if (componentClass == byte.class) {
			return byte[].class;
		}
		if (componentClass == char.class) {
			return char[].class;
		}
		if (componentClass == boolean.class) {
			return boolean[].class;
		}
		if (componentClass == short.class) {
			return short[].class;
		}
		if (componentClass == double.class) {
			return double[].class;
		}
		if (componentClass == float.class) {
			return float[].class;
		}
		if (componentClass == String.class) {
			return String[].class;
		}
		if (componentClass == Object.class) {
			return Object[].class;
		}
		return Array.newInstance(componentClass, 0).getClass();
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


	@SuppressWarnings({"rawtypes", "unchecked"})
	public static boolean isFunction(Class type) {
		if (type.isInterface()) {
			/*String typeName = type.getName();
			if (typeName.startsWith("java.util.function.")) {
				return true;
			}*/
			if (type.isAnnotationPresent(FunctionalInterface.class)) {
				return true;
			}
			int count = 0;
			Method[] methods = type.getMethods();
			for (Method method : methods) {
				if (!method.isDefault() && !Modifier.isStatic(method.getModifiers())) {
					count++;
					if (count > 1) {
						return false;
					}
				}
			}
			return count == 1;
		}
		return false;
	}

	/** 推测是否为Lambda表达式对象， */
	public static boolean isLambda(@Nullable Object obj) {
		if (obj == null) {
			return false;
		}
		Class<?> c = obj.getClass();
		if (!c.getSimpleName().contains("$$Lambda$")) {
			return false;
		}
		if (c.getSuperclass() != Object.class) {
			return false;
		}
		Class<?>[] interfaces = c.getInterfaces();
		if (interfaces.length != 1) {
			return false;
		}
		return isFunction(interfaces[0]);
	}

	public static boolean isEquals(Class<?>[] definedTypes, Class<?>[] checkedTypes) {
		if (definedTypes.length != checkedTypes.length) {
			return false;
		}
		for (int i = 0; i < definedTypes.length; i++) {
			if (definedTypes[i] != checkedTypes[i]) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAssignable(Class<?> definedType, Class<?> checkedType) {
		if (definedType.isPrimitive()) {
			return definedType == getPrimitiveClassByWrapper(checkedType);
		} else if (checkedType.isPrimitive()) {
			return getPrimitiveClassByWrapper(definedType) == checkedType;
		} else {
			return definedType.isAssignableFrom(checkedType);
		}
	}

	public static boolean isAssignable(Class<?>[] definedTypes, Class<?>[] checkedTypes) {
		if (definedTypes.length != checkedTypes.length) {
			return false;
		}
		for (int i = 0; i < definedTypes.length; i++) {
			if (!isAssignable(definedTypes[i], checkedTypes[i])) {
				return false;
			}
		}
		return true;
	}

}

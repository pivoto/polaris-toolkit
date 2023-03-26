package io.polaris.core.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class Types {
	private static final Map<Class, Class> primitiveTypes = new HashMap<>();

	{
		primitiveTypes.put(Integer.TYPE, Integer.class);
		primitiveTypes.put(Byte.TYPE, Byte.class);
		primitiveTypes.put(Short.TYPE, Short.class);
		primitiveTypes.put(Long.TYPE, Long.class);
		primitiveTypes.put(Character.TYPE, Character.class);
		primitiveTypes.put(Boolean.TYPE, Boolean.class);
		primitiveTypes.put(Float.TYPE, Float.class);
		primitiveTypes.put(Double.TYPE, Double.class);
	}

	/** 获取包装类型 */
	public static Class getWrapperClass(Class type) {
		if (!type.isPrimitive()) {
			return type;
		}
		return primitiveTypes.getOrDefault(type, type);
	}
}

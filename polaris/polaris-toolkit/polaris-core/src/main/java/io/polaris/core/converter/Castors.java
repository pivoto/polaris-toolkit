//package io.polaris.core.converter;
//
//import java.lang.reflect.Type;
//
///**
// * @author Qt
// * @since 1.8
// */
//public class Castors {
//
//
//	public static <T> T cast(Object obj, Class<T> clazz) {
//		if (clazz == null) {
//			throw new IllegalArgumentException("clazz is null");
//		}
//		if (obj == null) {
//			if (clazz == int.class) {
//				return (T) Integer.valueOf(0);
//			} else if (clazz == long.class) {
//				return (T) Long.valueOf(0);
//			} else if (clazz == short.class) {
//				return (T) Short.valueOf((short) 0);
//			} else if (clazz == byte.class) {
//				return (T) Byte.valueOf((byte) 0);
//			} else if (clazz == float.class) {
//				return (T) Float.valueOf(0);
//			} else if (clazz == double.class) {
//				return (T) Double.valueOf(0);
//			} else if (clazz == boolean.class) {
//				return (T) Boolean.FALSE;
//			}
//			return null;
//		}
//
//		if (clazz.isInstance(obj)) {
//			return (T) obj;
//		}
//
//		// 目前借用 fastjson 内部工具
//		return TypeUtils.cast(obj, clazz);
//	}
//
//	public static <T> T cast(Object obj, Type type) {
//		if (type instanceof Class) {
//			return (T) cast(obj, (Class) type);
//		}
//		return TypeUtils.cast(obj, type);
//	}
//}

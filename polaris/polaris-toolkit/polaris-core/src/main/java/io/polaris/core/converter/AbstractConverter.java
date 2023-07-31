package io.polaris.core.converter;

import io.polaris.core.lang.JavaType;

import java.lang.reflect.Type;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class AbstractConverter<T> implements Converter<T> {

	public JavaType<T> getTargetType() {
		Type actualType = JavaType.of(getClass()).getActualType(Converter.class, 0);
		return JavaType.of(actualType);
		//return (Class<T>) Reflects.findParameterizedType(Converter.class, getClass(), 0);
	}

	public final <S> T convert(Type valueType, S value) {
		return convert(JavaType.of(valueType), value);
	}

	public final <S> T convert(S value) {
		return convert(value.getClass(), value);
	}

	public final <S> T convertOrDefault(Type valueType, S value, T defaultValue) {
		T t = convert(valueType, value);
		if (t == null) {
			t = defaultValue;
		}
		return t;
	}

	public final <S> T convertOrDefault(S value, T defaultValue) {
		return convertOrDefault(value.getClass(), value, defaultValue);
	}


	@SuppressWarnings("unchecked")
	public final <S> T convert(JavaType<S> valueType, S value) {
		if (value == null) {
			Class<S> rawClass = valueType.getRawClass();
			if (rawClass.isPrimitive()) {
				if (Long.TYPE == rawClass) {
					return (T) Long.valueOf(0L);
				} else if (Boolean.TYPE == rawClass) {
					return (T) Boolean.FALSE;
				} else if (Character.TYPE == rawClass) {
					return (T) Character.valueOf((char) 0);
				} else if (Byte.TYPE == rawClass) {
					return (T) Byte.valueOf((byte) 0);
				} else if (Short.TYPE == rawClass) {
					return (T) Short.valueOf((short) 0);
				} else if (Integer.TYPE == rawClass) {
					return (T) Integer.valueOf((int) 0);
				} else if (Float.TYPE == rawClass) {
					return (T) Float.valueOf((float) 0);
				} else if (Double.TYPE == rawClass) {
					return (T) Double.valueOf((double) 0);
				}
			}
			return null;
		}
		JavaType<T> targetType = getTargetType();
		if (valueType.getRawType() == targetType.getRawType()) {
			// 类型完全一致
			return targetType.cast(value);
		}
		if (targetType.getRawType() instanceof Class && targetType.isInstance(value)) {
			// 无泛型且类型匹配
			return targetType.cast(value);
		}
		return doConvert(value, targetType, valueType);
	}

	protected abstract <S> T doConvert(S value, JavaType<T> targetType, JavaType<S> sourceType);
}

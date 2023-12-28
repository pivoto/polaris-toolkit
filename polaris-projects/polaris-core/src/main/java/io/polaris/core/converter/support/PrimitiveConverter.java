package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
public class PrimitiveConverter extends AbstractSimpleConverter<Object> {
	private final JavaType<Object> targetType;

	public PrimitiveConverter(Class<?> clazz) {
		if (!clazz.isPrimitive()) {
			throw new IllegalArgumentException();
		}
		this.targetType = (JavaType<Object>) JavaType.of(clazz);
	}

	@Override
	public JavaType<Object> getTargetType() {
		return targetType;
	}

	@Override
	protected String asString(Object value) {
		return Strings.trimToEmpty(super.asString(value));
	}

	@Override
	protected Object doConvert(Object value, JavaType<Object> targetType) {
		Class<?> primitiveClass = targetType.getRawClass();
		if (byte.class == primitiveClass) {
			return Converters.convertQuietly(Byte.class, value, (byte) 0).byteValue();
		} else if (short.class == primitiveClass) {
			return Converters.convertQuietly(Short.class, value, (short) 0).shortValue();
		} else if (int.class == primitiveClass) {
			return Converters.convertQuietly(Integer.class, value, (int) 0).intValue();
		} else if (long.class == primitiveClass) {
			return Converters.convertQuietly(Long.class, value, (long) 0L).longValue();
		} else if (float.class == primitiveClass) {
			return Converters.convertQuietly(Float.class, value, 0f).floatValue();
		} else if (double.class == primitiveClass) {
			return Converters.convertQuietly(Double.class, value, 0d).doubleValue();
		} else if (char.class == primitiveClass) {
			return Converters.convertQuietly(Character.class, value, '\0').charValue();
		} else if (boolean.class == primitiveClass) {
			return Converters.convertQuietly(Boolean.class, value, false).booleanValue();
		}
		throw new IllegalArgumentException();
	}
}

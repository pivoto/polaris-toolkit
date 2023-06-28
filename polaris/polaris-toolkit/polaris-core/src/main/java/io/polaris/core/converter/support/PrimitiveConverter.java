package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
public class PrimitiveConverter extends AbstractConverter<Object> {
	private final Class<?> targetType;

	public PrimitiveConverter(Class<?> clazz) {
		if (!clazz.isPrimitive()) {
			throw new IllegalArgumentException();
		}
		this.targetType = clazz;
	}

	public Class<Object> getTargetType() {
		return (Class<Object>) this.targetType;
	}

	@Override
	protected String convertToStr(Object value) {
		return Strings.trimToEmpty(super.convertToStr(value));
	}

	@Override
	protected Object convertInternal(Object value, Class<?> primitiveClass) {
		if (byte.class == primitiveClass) {
			return ConverterRegistry.INSTANCE.convertQuietly(Byte.class, value, (byte) 0).byteValue();
		} else if (short.class == primitiveClass) {
			return ConverterRegistry.INSTANCE.convertQuietly(Short.class, value, (short) 0).shortValue();
		} else if (int.class == primitiveClass) {
			return ConverterRegistry.INSTANCE.convertQuietly(Integer.class, value, (int) 0).intValue();
		} else if (long.class == primitiveClass) {
			return ConverterRegistry.INSTANCE.convertQuietly(Long.class, value, (long) 0L).longValue();
		} else if (float.class == primitiveClass) {
			return ConverterRegistry.INSTANCE.convertQuietly(Float.class, value, 0f).floatValue();
		} else if (double.class == primitiveClass) {
			return ConverterRegistry.INSTANCE.convertQuietly(Double.class, value, 0d).doubleValue();
		} else if (char.class == primitiveClass) {
			return ConverterRegistry.INSTANCE.convertQuietly(Character.class, value, '\0').charValue();
		} else if (boolean.class == primitiveClass) {
			return ConverterRegistry.INSTANCE.convertQuietly(Boolean.class, value, false).booleanValue();
		}
		throw new IllegalArgumentException();
	}
}

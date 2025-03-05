package io.polaris.core.converter;

import io.polaris.core.lang.JavaType;

/**
 * @author Qt
 * @since 1.8
 */
public class EnumConverter<T extends Enum<T>> extends AbstractSimpleConverter<T> {
	private final JavaType<T> targetType ;

	public EnumConverter(Class<T> enumClass) {
		this.targetType = JavaType.of(enumClass);
	}
	@Override
	public JavaType<T> getTargetType() {
		return targetType;
	}

	@Override
	protected T doConvert(Object value, JavaType<T> targetType) {
		if (value == null) {
			return null;
		}
		T[] enumConstants = targetType.getRawClass().getEnumConstants();
		if (value instanceof Number) {
			int i = ((Number) value).intValue();
			return i >= 0 && i < enumConstants.length ? enumConstants[i] : null;
		} else {
			return (T) Enum.valueOf(targetType.getRawClass(), asSimpleString(value));
		}
	}

}

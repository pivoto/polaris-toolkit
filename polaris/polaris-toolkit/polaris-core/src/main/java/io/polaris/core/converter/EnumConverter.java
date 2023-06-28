package io.polaris.core.converter;

/**
 * @author Qt
 * @since 1.8
 */
public class EnumConverter<T extends Enum<T>> extends AbstractConverter<T> {
	private final Class<T> enumClass;

	public EnumConverter(Class<T> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public Class<T> getTargetType() {
		return this.enumClass;
	}

	@Override
	protected T convertInternal(Object value, Class<? extends T> targetType) {
		if (value == null) {
			return null;
		}
		T[] enumConstants = enumClass.getEnumConstants();
		if (value instanceof Number) {
			int i = ((Number) value).intValue();
			return i >= 0 && i < enumConstants.length ? enumConstants[i] : null;
		} else {
			return (T) Enum.valueOf(enumClass, convertToStr(value));
		}
	}

}

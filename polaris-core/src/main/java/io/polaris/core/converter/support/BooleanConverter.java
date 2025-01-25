package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.primitive.Booleans;

/**
 * @author Qt
 * @since 1.8
 */
public class BooleanConverter extends AbstractSimpleConverter<Boolean> {
	private final JavaType<Boolean> targetType = JavaType.of(Boolean.class);

	@Override
	public JavaType<Boolean> getTargetType() {
		return targetType;
	}

	@Override
	protected Boolean doConvert(Object value, JavaType<Boolean> targetType) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof Number) {
			// 0为false，其它数字为true
			return 0 != ((Number) value).intValue();
		}
		return Booleans.parseBoolean(asString(value));
	}
}

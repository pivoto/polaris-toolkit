package io.polaris.core.converter.support;

import javax.annotation.Nullable;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.jdbc.sql.query.StringValue;
import io.polaris.core.lang.JavaType;

/**
 * @author Qt
 * @since Dec 20, 2025
 */
public class StringValueConverter extends AbstractSimpleConverter<StringValue> {

	private final JavaType<StringValue> targetType = JavaType.of(StringValue.class);

	public StringValueConverter() {
	}

	@Override
	public JavaType<StringValue> getTargetType() {
		return targetType;
	}

	@Override
	protected StringValue doConvert(@Nullable Object value, JavaType<StringValue> targetType) {
		String targetValue = asSimpleString(value);
		if (targetValue == null) {
			return null;
		}
		return new StringValue(targetValue);
	}
}

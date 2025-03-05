package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;

import java.util.TimeZone;

/**
 * @author Qt
 * @since 1.8
 */
public class TimeZoneConverter extends AbstractSimpleConverter<TimeZone> {
	private final JavaType<TimeZone> targetType = JavaType.of(TimeZone.class);

	@Override
	public JavaType<TimeZone> getTargetType() {
		return targetType;
	}

	@Override
	protected TimeZone doConvert(Object value, JavaType<TimeZone> targetType) {
		return TimeZone.getTimeZone(asSimpleString(value));
	}
}

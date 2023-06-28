package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;

import java.util.TimeZone;

/**
 * @author Qt
 * @since 1.8
 */
public class TimeZoneConverter extends AbstractConverter<TimeZone> {
	@Override
	protected TimeZone convertInternal(Object value, Class<? extends TimeZone> targetType) {
		return TimeZone.getTimeZone(convertToStr(value));
	}
}

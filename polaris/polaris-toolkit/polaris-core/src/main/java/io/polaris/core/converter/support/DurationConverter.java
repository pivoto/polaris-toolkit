package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

/**
 * @author Qt
 * @since 1.8
 */
public class DurationConverter extends AbstractConverter<Duration> {
	@Override
	protected Duration convertInternal(Object value, Class<? extends Duration> targetType) {
		if(value instanceof TemporalAmount){
			return Duration.from((TemporalAmount) value);
		} else if(value instanceof Long){
			return Duration.ofMillis((Long) value);
		} else {
			return Duration.parse(convertToStr(value));
		}
	}
}

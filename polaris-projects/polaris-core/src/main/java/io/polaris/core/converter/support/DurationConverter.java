package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

/**
 * @author Qt
 * @since 1.8
 */
public class DurationConverter extends AbstractSimpleConverter<Duration> {
	private final JavaType<Duration> targetType = JavaType.of(Duration.class);

	@Override
	public JavaType<Duration> getTargetType() {
		return targetType;
	}
	@Override
	protected Duration doConvert(Object value, JavaType<Duration> targetType) {
		if(value instanceof TemporalAmount){
			return Duration.from((TemporalAmount) value);
		} else if(value instanceof Long){
			return Duration.ofMillis((Long) value);
		} else {
			return Duration.parse(asString(value));
		}
	}
}

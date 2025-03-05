package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;

import java.time.Period;
import java.time.temporal.TemporalAmount;

/**
 * @author Qt
 * @since 1.8
 */
public class PeriodConverter extends AbstractSimpleConverter<Period> {
	private final JavaType<Period> targetType = JavaType.of(Period.class);

	@Override
	public JavaType<Period> getTargetType() {
		return targetType;
	}

	@Override
	protected Period doConvert(Object value, JavaType<Period> targetType) {
		if (value instanceof TemporalAmount) {
			return Period.from((TemporalAmount) value);
		} else if (value instanceof Integer) {
			return Period.ofDays((Integer) value);
		} else {
			return Period.parse(asSimpleString(value));
		}
	}
}

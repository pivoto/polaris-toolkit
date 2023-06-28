package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;

import java.time.Period;
import java.time.temporal.TemporalAmount;

/**
 * @author Qt
 * @since 1.8
 */
public class PeriodConverter extends AbstractConverter<Period> {
	@Override
	protected Period convertInternal(Object value, Class<? extends Period> targetType) {
		if(value instanceof TemporalAmount){
			return Period.from((TemporalAmount) value);
		}else if(value instanceof Integer){
			return Period.ofDays((Integer) value);
		} else {
			return Period.parse(convertToStr(value));
		}
	}
}

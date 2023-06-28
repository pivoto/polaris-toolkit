package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.date.Dates;
import io.polaris.core.string.Strings;
import lombok.Getter;
import lombok.Setter;

import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;


/**
 * @author Qt
 * @since 1.8
 */
public class DateConverter extends AbstractConverter<Date> {
	private final Class<? extends Date> targetType;
	@Getter
	@Setter
	private String format;

	public DateConverter(Class<? extends Date> targetType) {
		this.targetType = targetType;
	}

	public DateConverter(Class<? extends Date> targetType, String format) {
		this.targetType = targetType;
		this.format = format;
	}

	@Override
	protected Date convertInternal(Object value, Class<? extends Date> targetType) {
		if (value == null || (value instanceof CharSequence && Strings.isBlank(value.toString()))) {
			return null;
		}
		Date date = parseDate(value);
		if (java.util.Date.class == targetType) {
			return date;
		}
		if (java.sql.Date.class == targetType) {
			return new java.sql.Date(date.getTime());
		}
		if (java.sql.Time.class == targetType) {
			return new java.sql.Time(date.getTime());
		}
		if (java.sql.Timestamp.class == targetType) {
			return new java.sql.Timestamp(date.getTime());
		}
		return date;
	}

	private Date parseDate(Object value) {
		if (value instanceof Date) {
			return (Date) value;
		}
		if (value instanceof TemporalAccessor) {
			return Dates.toDate((TemporalAccessor) value);
		}
		if (value instanceof Calendar) {
			return Dates.toDate(((Calendar) value).getTimeInMillis());
		}
		if (value instanceof Number) {
			return Dates.toDate(((Number) value).longValue());
		}
		String valueStr = convertToStr(value);
		Date date = Strings.isBlank(format) ? Dates.parseDate(valueStr) : Dates.parseDate(format, valueStr);
		return date;
	}
}

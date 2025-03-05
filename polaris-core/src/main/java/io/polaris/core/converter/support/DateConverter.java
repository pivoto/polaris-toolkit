package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.time.Dates;
import io.polaris.core.lang.JavaType;
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
public class DateConverter<T extends Date> extends AbstractSimpleConverter<T> {
	private final JavaType<T> targetType;
	@Getter
	@Setter
	private String format;

	public DateConverter(JavaType<T> targetType) {
		this.targetType = targetType;
	}

	public DateConverter(Class<T> targetType) {
		this.targetType = JavaType.of(targetType);
	}

	public DateConverter(Class<T> targetType, String format) {
		this.targetType = JavaType.of(targetType);
		this.format = format;
	}

	public DateConverter(JavaType<T> targetType, String format) {
		this.targetType = targetType;
		this.format = format;
	}

	@Override
	public JavaType<T> getTargetType() {
		return targetType;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	protected T doConvert(Object value, JavaType<T> type) {
		if (value == null || (value instanceof CharSequence && Strings.isBlank(value.toString()))) {
			return null;
		}
		Class rawClass = type.getRawClass();
		Date date = parseDate(value);
		if (java.util.Date.class == rawClass) {
			return (T) date;
		}
		if (java.sql.Date.class == rawClass) {
			return (T) new java.sql.Date(date.getTime());
		}
		if (java.sql.Time.class == rawClass) {
			return (T) new java.sql.Time(date.getTime());
		}
		if (java.sql.Timestamp.class == rawClass) {
			return (T) new java.sql.Timestamp(date.getTime());
		}
		return (T) date;
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
		String valueStr = asSimpleString(value);
		Date date = Strings.isBlank(format) ? Dates.parseDate(valueStr) : Dates.parseDate(format, valueStr);
		return date;
	}
}

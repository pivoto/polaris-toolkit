package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.converter.ConversionException;
import io.polaris.core.date.Dates;
import io.polaris.core.lang.JavaType;
import io.polaris.core.string.Strings;
import lombok.Getter;
import lombok.Setter;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Qt
 * @since 1.8
 */
public class TemporalAccessorConverter<T extends TemporalAccessor> extends AbstractSimpleConverter<T> {
	private final JavaType<T> targetType;
	@Getter
	@Setter
	private String format;

	public TemporalAccessorConverter(Class<T> targetType) {
		this(targetType, null);
	}

	public TemporalAccessorConverter(Class<T> targetType, String format) {
		this(JavaType.of(targetType), format);
	}

	public TemporalAccessorConverter(JavaType<T> targetType, String format) {
		this.targetType = targetType;
		this.format = format;
	}

	@Override
	public JavaType<T> getTargetType() {
		return targetType;
	}

	@Override
	protected T doConvert(Object value, JavaType<T> targetType) {
		if (value == null || (value instanceof CharSequence && Strings.isBlank(value.toString()))) {
			return null;
		}
		LocalDateTime localDateTime = parseTemporalAccessor(value);
		Class<T> raw = this.targetType.getRawClass();
		if (LocalDateTime.class.equals(raw)) {
			return (T) localDateTime;
		}
		if (Instant.class.equals(raw)) {
			return (T) Dates.toDate(localDateTime).toInstant();
		}
		if (LocalDate.class.equals(raw)) {
			return (T) Dates.toLocalDate(localDateTime);
		}
		if (LocalTime.class.equals(raw)) {
			return (T) Dates.toLocalTime(localDateTime);
		}
		if (ZonedDateTime.class.equals(raw)) {
			return (T) ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
		}
		if (OffsetDateTime.class.equals(raw)) {
			return (T) OffsetDateTime.ofInstant(Dates.toInstant(localDateTime), ZoneId.systemDefault());
		}
		if (OffsetTime.class.equals(raw)) {
			return (T) OffsetTime.ofInstant(Dates.toInstant(localDateTime), ZoneId.systemDefault());
		}
		if (DayOfWeek.class.equals(raw)) {
			return (T) DayOfWeek.from(localDateTime);
		}
		if (Month.class.equals(raw)) {
			return (T) Month.from(localDateTime);
		}
		if (MonthDay.class.equals(raw)) {
			return (T) MonthDay.from(localDateTime);
		}
		if (Year.class.equals(raw)) {
			return (T) Year.from(localDateTime);
		}
		if (YearMonth.class.equals(raw)) {
			return (T) YearMonth.from(localDateTime);
		}
		throw new ConversionException();
	}


	private LocalDateTime parseTemporalAccessor(Object value) {
		if (value instanceof TemporalAccessor) {
			return Dates.toLocalDateTime((TemporalAccessor) value);
		}
		if (value instanceof Date) {
			return Dates.toLocalDateTime((Date) value);
		}
		if (value instanceof Calendar) {
			return Dates.toLocalDateTime(((Calendar) value).getTimeInMillis());
		}
		if (value instanceof Number) {
			return Dates.toLocalDateTime(((Number) value).longValue());
		}
		String valueStr = asString(value);
		return Strings.isBlank(format) ? Dates.parseLocalDateTime(valueStr) : Dates.parseLocalDateTime(format, valueStr);
	}
}

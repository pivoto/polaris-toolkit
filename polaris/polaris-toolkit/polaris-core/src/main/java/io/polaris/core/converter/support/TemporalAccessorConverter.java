package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.date.Dates;
import io.polaris.core.string.Strings;
import lombok.Getter;
import lombok.Setter;

import java.time.*;
import java.time.chrono.IsoEra;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Qt
 * @since 1.8
 */
public class TemporalAccessorConverter extends AbstractConverter<TemporalAccessor> {
	private final Class<?> targetType;
	@Getter
	@Setter
	private String format;

	public TemporalAccessorConverter(Class<?> targetType) {
		this(targetType, null);
	}

	public TemporalAccessorConverter(Class<?> targetType, String format) {
		this.targetType = targetType;
		this.format = format;
	}

	@Override
	public Class<TemporalAccessor> getTargetType() {
		return (Class<TemporalAccessor>) this.targetType;
	}

	@Override
	protected TemporalAccessor convertInternal(Object value, Class<? extends TemporalAccessor> targetType) {
		if (value == null || (value instanceof CharSequence && Strings.isBlank(value.toString()))) {
			return null;
		}
		LocalDateTime localDateTime = parseTemporalAccessor(value);
		if (LocalDateTime.class.equals(this.targetType)) {
			return localDateTime;
		}
		if (Instant.class.equals(this.targetType)) {
			return Dates.toDate(localDateTime).toInstant();
		}
		if (LocalDate.class.equals(this.targetType)) {
			return Dates.toLocalDate(localDateTime);
		}
		if (LocalTime.class.equals(this.targetType)) {
			return Dates.toLocalTime(localDateTime);
		}
		if (ZonedDateTime.class.equals(this.targetType)) {
			return ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
		}
		if (OffsetDateTime.class.equals(this.targetType)) {
			return OffsetDateTime.ofInstant(Dates.toInstant(localDateTime), ZoneId.systemDefault());
		}
		if (OffsetTime.class.equals(this.targetType)) {
			return OffsetTime.ofInstant(Dates.toInstant(localDateTime), ZoneId.systemDefault());
		}
		if (DayOfWeek.class.equals(this.targetType)) {
			return DayOfWeek.from(localDateTime);
		}
		if (Month.class.equals(this.targetType)) {
			return Month.from(localDateTime);
		}
		if (MonthDay.class.equals(this.targetType)) {
			return MonthDay.from(localDateTime);
		}
		if (Year.class.equals(this.targetType)) {
			return Year.from(localDateTime);
		}
		if (YearMonth.class.equals(this.targetType)) {
			return YearMonth.from(localDateTime);
		}
		throw new UnsupportedOperationException();
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
		String valueStr = convertToStr(value);
		return Strings.isBlank(format) ? Dates.parseLocalDateTime(valueStr) : Dates.parseLocalDateTime(format, valueStr);
	}
}

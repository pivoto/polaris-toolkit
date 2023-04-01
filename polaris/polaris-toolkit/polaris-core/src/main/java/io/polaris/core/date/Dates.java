package io.polaris.core.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since 1.8
 */
public class Dates {
	private static Map<String, DateTimeFormatter> formatterCache = new ConcurrentHashMap<>();

	public static final DateTimeFormatter YYYYMMDD = getFormatter("yyyyMMdd");
	public static final DateTimeFormatter YYYY_MM_DD = getFormatter("yyyy-MM-dd");
	public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = getFormatter("yyyy-MM-dd HH:mm:ss");
	public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSS = getFormatter("yyyy-MM-dd HH:mm:ss.SSS");
	public static final DateTimeFormatter HH_MM_SS = getFormatter("HH:mm:ss");
	public static final DateTimeFormatter HH_MM_SS_SSS = getFormatter("HH:mm:ss.SSS");


	public static DateTimeFormatter getFormatter(String formatterStr) {
		DateTimeFormatter formatter = formatterCache.get(formatterStr);
		if (formatter == null) {
			formatterCache.putIfAbsent(formatterStr, DateTimeFormatter.ofPattern(formatterStr).withZone(ZoneId.systemDefault()));
			formatter = formatterCache.get(formatterStr);
		}
		return formatter;
	}

	public static String getDefaultDateTimeStr() {
		return YYYY_MM_DD_HH_MM_SS_SSS.format(LocalDateTime.now());
	}

	public static String getDefaultDateTimeStr(LocalDateTime localDateTime) {
		return YYYY_MM_DD_HH_MM_SS_SSS.format(localDateTime);
	}

	public static String format(String format, TemporalAccessor temporal) {
		return getFormatter(format).format(temporal);
	}

	public static TemporalAccessor parse(String format, String temporal) {
		return getFormatter(format).parse(temporal);
	}

	public static Date parseDate(String format, String temporal) {
		return toDate(getFormatter(format).parse(temporal));
	}

	public static LocalDateTime parseLocalDateTime(String format, String temporal) {
		return toLocalDateTime(getFormatter(format).parse(temporal));
	}

	public static LocalDate parseLocalDate(String format, String temporal) {
		return toLocalDate(getFormatter(format).parse(temporal));
	}

	public static LocalTime parseLocalTime(String format, String temporal) {
		return toLocalTime(getFormatter(format).parse(temporal));
	}


	public static long toMills(Date date) {
		return date.getTime();
	}

	public static Date toDate(long mills) {
		return new Date(mills);
	}


	public static long toMills(LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static LocalDateTime toLocalDateTime(long milli) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(milli), ZoneId.systemDefault());
	}

	public static long toMills(Instant instant) {
		return instant.toEpochMilli();
	}

	public static Instant toInstant(long milli) {
		return Instant.ofEpochMilli(milli);
	}

	public static Date toDate(Instant instant) {
		return Date.from(instant);
	}

	public static Instant toInstant(Date date) {
		return date.toInstant();
	}

	public static Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDateTime toLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	public static Date toDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate toLocalDate(Date date) {
		return toLocalDateTime(date).toLocalDate();
	}

	public static LocalDate toLocalDate(TemporalAccessor temporal) {
		if (temporal instanceof LocalDate) {
			return (LocalDate) temporal;
		} else if (temporal instanceof LocalDateTime) {
			return ((LocalDateTime) temporal).toLocalDate();
		} else if (temporal instanceof ZonedDateTime) {
			return ((ZonedDateTime) temporal).toLocalDate();
		} else if (temporal instanceof OffsetDateTime) {
			return ((OffsetDateTime) temporal).toLocalDate();
		}
		int year = getChronoFieldOrDefault(temporal, ChronoField.YEAR);
		int month = getChronoFieldOrDefault(temporal, ChronoField.MONTH_OF_YEAR);
		int day = getChronoFieldOrDefault(temporal, ChronoField.DAY_OF_MONTH);
		return LocalDate.of(year, month, day);
	}

	public static LocalTime toLocalTime(TemporalAccessor temporal) {
		if (temporal instanceof LocalTime) {
			return (LocalTime) temporal;
		} else if (temporal instanceof ZonedDateTime) {
			return ((ZonedDateTime) temporal).toLocalTime();
		} else if (temporal instanceof OffsetDateTime) {
			return ((OffsetDateTime) temporal).toLocalTime();
		}
		int hour = getChronoFieldOrDefault(temporal, ChronoField.HOUR_OF_DAY);
		int minute = getChronoFieldOrDefault(temporal, ChronoField.MINUTE_OF_HOUR);
		int second = getChronoFieldOrDefault(temporal, ChronoField.SECOND_OF_MINUTE);
		int nano = getChronoFieldOrDefault(temporal, ChronoField.NANO_OF_SECOND);
		return LocalTime.of(hour, minute, second, nano);
	}

	public static LocalDateTime toLocalDateTime(TemporalAccessor temporal) {
		if (temporal instanceof LocalDateTime) {
			return (LocalDateTime) temporal;
		} else if (temporal instanceof ZonedDateTime) {
			return ((ZonedDateTime) temporal).toLocalDateTime();
		} else if (temporal instanceof OffsetDateTime) {
			return ((OffsetDateTime) temporal).toLocalDateTime();
		}
		int year = getChronoFieldOrDefault(temporal, ChronoField.YEAR);
		int month = getChronoFieldOrDefault(temporal, ChronoField.MONTH_OF_YEAR);
		int day = getChronoFieldOrDefault(temporal, ChronoField.DAY_OF_MONTH);
		int hour = getChronoFieldOrDefault(temporal, ChronoField.HOUR_OF_DAY);
		int minute = getChronoFieldOrDefault(temporal, ChronoField.MINUTE_OF_HOUR);
		int second = getChronoFieldOrDefault(temporal, ChronoField.SECOND_OF_MINUTE);
		int nano = getChronoFieldOrDefault(temporal, ChronoField.NANO_OF_SECOND);
		return LocalDateTime.of(year, month, day, hour, minute, second, nano);
	}

	public static Date toDate(TemporalAccessor temporal) {
		return toDate(toLocalDateTime(temporal));
	}

	private static int getChronoFieldOrDefault(TemporalAccessor temporal, ChronoField field) {
		return (temporal.isSupported(field)) ?
			temporal.get(field) : (int) field.range().getMinimum();
	}

}

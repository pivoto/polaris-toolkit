package io.polaris.core.date;

import java.time.*;
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

	public static final String PATTERN_YYYYMMDD = "yyyyMMdd";
	public static final DateTimeFormatter YYYYMMDD = getFormatter(PATTERN_YYYYMMDD);
	public static final String PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final DateTimeFormatter YYYYMMDDHHMMSS = getFormatter(PATTERN_YYYYMMDDHHMMSS);
	public static final String PATTERN_YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
	public static final DateTimeFormatter YYYYMMDDHHMMSSSSS = getFormatter(PATTERN_YYYYMMDDHHMMSSSSS);
	public static final String PATTERN_HHMMSS = "HHmmss";
	public static final DateTimeFormatter HHMMSS = getFormatter(PATTERN_HHMMSS);
	public static final String PATTERN_HHMMSSSSS = "HHmmssSSS";
	public static final DateTimeFormatter HHMMSSSSS = getFormatter(PATTERN_HHMMSSSSS);
	public static final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";
	public static final DateTimeFormatter YYYY_MM_DD = getFormatter(PATTERN_YYYY_MM_DD);
	public static final String PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = getFormatter(PATTERN_YYYY_MM_DD_HH_MM_SS);
	public static final String PATTERN_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSS = getFormatter(PATTERN_YYYY_MM_DD_HH_MM_SS_SSS);
	public static final String PATTERN_HH_MM_SS = "HH:mm:ss";
	public static final DateTimeFormatter HH_MM_SS = getFormatter(PATTERN_HH_MM_SS);
	public static final String PATTERN_HH_MM_SS_SSS = "HH:mm:ss.SSS";
	public static final DateTimeFormatter HH_MM_SS_SSS = getFormatter(PATTERN_HH_MM_SS_SSS);


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

	public static Instant toInstant(LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
	}

	public static Instant toInstant(LocalDate localDate) {
		return localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
	}

	public static Date toDate(LocalDateTime localDateTime) {
		return Date.from(toInstant(localDateTime));
	}


	public static LocalDateTime toLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	public static Date toDate(LocalDate localDate) {
		return Date.from(toInstant(localDate));
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

	public static TemporalAccessor parse(String valueStr) {
		TemporalAccessor accessor = null;
		try {
			if (valueStr.length() == Dates.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS.length()) {
				accessor = Dates.YYYY_MM_DD_HH_MM_SS_SSS.parse(valueStr);
			} else if (valueStr.length() == Dates.PATTERN_YYYY_MM_DD_HH_MM_SS.length()) {
				accessor = Dates.YYYY_MM_DD_HH_MM_SS.parse(valueStr);
			} else if (valueStr.length() == Dates.PATTERN_YYYY_MM_DD.length()) {
				accessor = Dates.YYYY_MM_DD.parse(valueStr);
			} else if (valueStr.length() == Dates.PATTERN_YYYYMMDDHHMMSSSSS.length()) {
				accessor = Dates.YYYYMMDDHHMMSSSSS.parse(valueStr);
			} else if (valueStr.length() == Dates.PATTERN_YYYYMMDDHHMMSS.length()) {
				accessor = Dates.YYYYMMDDHHMMSS.parse(valueStr);
			} else if (valueStr.length() == Dates.PATTERN_YYYYMMDD.length()) {
				try {
					accessor = Dates.YYYYMMDD.parse(valueStr);
				} catch (Exception e) {
					accessor = Dates.HH_MM_SS.parse(valueStr);
				}
			} else if (valueStr.length() == Dates.PATTERN_HH_MM_SS_SSS.length()) {
				accessor = Dates.HH_MM_SS_SSS.parse(valueStr);
			}
		} catch (Exception e) {
		}
		if (accessor == null) {
			accessor = DateTimeFormatter.ISO_DATE_TIME.parse(valueStr);
		}
		return accessor;
	}

	public static Date parseDate(String valueStr) {
		return Dates.toDate(parse(valueStr));
	}

	public static LocalDateTime parseLocalDateTime(String valueStr) {
		return toLocalDateTime(parse(valueStr));
	}

	public static LocalDate parseLocalDate(String valueStr) {
		return toLocalDate(parse(valueStr));
	}

	public static LocalTime parseLocalTime(String valueStr) {
		return toLocalTime(parse(valueStr));
	}

}

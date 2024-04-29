package io.polaris.core.time;

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
@SuppressWarnings("SpellCheckingInspection")
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
		return formatterCache.computeIfAbsent(formatterStr, key ->
			DateTimeFormatter.ofPattern(formatterStr).withZone(ZoneId.systemDefault())
		);
	}

	public static Instant now() {
		return Instant.now();
	}

	public static String nowStr() {
		return YYYY_MM_DD_HH_MM_SS_SSS.format(LocalDateTime.now());
	}

	public static Date nowDate() {
		return new Date();
	}

	public static String formatDefault(LocalDateTime localDateTime) {
		return YYYY_MM_DD_HH_MM_SS_SSS.format(localDateTime);
	}

	public static String formatDefault(TemporalAccessor temporal) {
		return YYYY_MM_DD_HH_MM_SS_SSS.format(toLocalDateTime(temporal));
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


	public static long toMills(Date date) {
		return date.getTime();
	}

	public static long toMills(LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static long toMills(Instant instant) {
		return instant.toEpochMilli();
	}


	public static long toMills(TemporalAccessor temporal) {
		return toMills(toLocalDateTime(temporal));
	}


	public static Date toDate(long mills) {
		return new Date(mills);
	}

	public static Date toDate(Instant instant) {
		return Date.from(instant);
	}

	public static Date toDate(LocalDateTime localDateTime) {
		return Date.from(toInstant(localDateTime));
	}

	public static Date toDate(LocalDate localDate) {
		return Date.from(toInstant(localDate));
	}


	public static Date toDate(TemporalAccessor temporal) {
		return toDate(toLocalDateTime(temporal));
	}


	public static Instant toInstant(long mills) {
		return Instant.ofEpochMilli(mills);
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

	public static Instant toInstant(TemporalAccessor temporal) {
		return toInstant(toLocalDateTime(temporal));
	}

	public static LocalDateTime toLocalDateTime(long mills) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(mills), ZoneId.systemDefault());
	}

	public static LocalDateTime toLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	public static LocalDateTime toLocalDateTime(TemporalAccessor temporal) {
		if (temporal instanceof LocalDateTime) {
			return (LocalDateTime) temporal;
		} else if (temporal instanceof Instant) {
			return LocalDateTime.ofInstant((Instant) temporal, ZoneId.systemDefault());
		} else if (temporal instanceof ZonedDateTime) {
			return ((ZonedDateTime) temporal).toLocalDateTime();
		} else if (temporal instanceof OffsetDateTime) {
			return ((OffsetDateTime) temporal).toLocalDateTime();
		} else if (temporal instanceof LocalDate) {
			return LocalDateTime.of((LocalDate) temporal, LocalTime.of(0, 0, 0, 0));
		} else if (temporal instanceof LocalTime) {
			return LocalDateTime.of(LocalDate.now(), (LocalTime) temporal);
		} else if (temporal instanceof OffsetTime) {
			return LocalDateTime.of(LocalDate.now(), ((OffsetTime) temporal).toLocalTime());
		} else if (temporal instanceof YearMonth) {
			return LocalDateTime.of(((YearMonth) temporal).atDay(1), LocalTime.of(0, 0, 0, 0));
		} else if (temporal instanceof Year) {
			return LocalDateTime.of(((Year) temporal).atMonth(1).atDay(1), LocalTime.of(0, 0, 0, 0));
		} else if (temporal instanceof Month) {
			return LocalDateTime.of(LocalDate.now().with(ChronoField.MONTH_OF_YEAR, ((Month) temporal).getValue()), LocalTime.of(0, 0, 0, 0));
		} else if (temporal instanceof MonthDay) {
			return LocalDateTime.of(LocalDate.now().with(ChronoField.MONTH_OF_YEAR, ((MonthDay) temporal).getMonth().getValue()).with(ChronoField.DAY_OF_MONTH, ((MonthDay) temporal).getDayOfMonth()), LocalTime.of(0, 0, 0, 0));
		} else if (temporal instanceof DayOfWeek) {
			return LocalDateTime.of(LocalDate.now().with(ChronoField.DAY_OF_WEEK, ((DayOfWeek) temporal).getValue()), LocalTime.of(0, 0, 0, 0));
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


	public static LocalDate toLocalDate(long mills) {
		return toLocalDateTime(mills).toLocalDate();
	}

	public static LocalDate toLocalDate(Date date) {
		return toLocalDateTime(date).toLocalDate();
	}

	public static LocalDate toLocalDate(TemporalAccessor temporal) {
		if (temporal instanceof LocalDate) {
			return (LocalDate) temporal;
		} else if (temporal instanceof Instant) {
			return ((Instant) temporal).atZone(ZoneId.systemDefault()).toLocalDate();
		} else if (temporal instanceof LocalDateTime) {
			return ((LocalDateTime) temporal).toLocalDate();
		} else if (temporal instanceof ZonedDateTime) {
			return ((ZonedDateTime) temporal).toLocalDate();
		} else if (temporal instanceof OffsetDateTime) {
			return ((OffsetDateTime) temporal).toLocalDate();
		} else if (temporal instanceof LocalTime) {
			return LocalDate.now();
		} else if (temporal instanceof OffsetTime) {
			return LocalDate.now();
		} else if (temporal instanceof YearMonth) {
			return ((YearMonth) temporal).atDay(1);
		} else if (temporal instanceof Year) {
			return ((Year) temporal).atMonth(1).atDay(1);
		} else if (temporal instanceof Month) {
			return LocalDate.now().with(ChronoField.MONTH_OF_YEAR, ((Month) temporal).getValue());
		} else if (temporal instanceof MonthDay) {
			return LocalDate.now().with(ChronoField.MONTH_OF_YEAR, ((MonthDay) temporal).getMonth().getValue()).with(ChronoField.DAY_OF_MONTH, ((MonthDay) temporal).getDayOfMonth());
		} else if (temporal instanceof DayOfWeek) {
			return LocalDate.now().with(ChronoField.DAY_OF_WEEK, ((DayOfWeek) temporal).getValue());
		}
		int year = getChronoFieldOrDefault(temporal, ChronoField.YEAR);
		int month = getChronoFieldOrDefault(temporal, ChronoField.MONTH_OF_YEAR);
		int day = getChronoFieldOrDefault(temporal, ChronoField.DAY_OF_MONTH);
		return LocalDate.of(year, month, day);
	}

	public static LocalTime toLocalTime(long mills) {
		return toLocalDateTime(mills).toLocalTime();
	}

	public static LocalTime toLocalTime(Date date) {
		return toLocalDateTime(date).toLocalTime();
	}

	public static LocalTime toLocalTime(TemporalAccessor temporal) {
		if (temporal instanceof LocalTime) {
			return (LocalTime) temporal;
		} else if (temporal instanceof Instant) {
			return LocalDateTime.ofInstant((Instant) temporal, ZoneId.systemDefault()).toLocalTime();
		} else if (temporal instanceof ZonedDateTime) {
			return ((ZonedDateTime) temporal).toLocalTime();
		} else if (temporal instanceof OffsetDateTime) {
			return ((OffsetDateTime) temporal).toLocalTime();
		} else if (temporal instanceof LocalDate) {
			return LocalTime.of(0, 0, 0, 0);
		} else if (temporal instanceof OffsetTime) {
			return ((OffsetTime) temporal).toLocalTime();
		} else if (temporal instanceof YearMonth) {
			return LocalTime.of(0, 0, 0, 0);
		} else if (temporal instanceof Year) {
			return LocalTime.of(0, 0, 0, 0);
		} else if (temporal instanceof Month) {
			return LocalTime.of(0, 0, 0, 0);
		} else if (temporal instanceof MonthDay) {
			return LocalTime.of(0, 0, 0, 0);
		} else if (temporal instanceof DayOfWeek) {
			return LocalTime.of(0, 0, 0, 0);
		}
		int hour = getChronoFieldOrDefault(temporal, ChronoField.HOUR_OF_DAY);
		int minute = getChronoFieldOrDefault(temporal, ChronoField.MINUTE_OF_HOUR);
		int second = getChronoFieldOrDefault(temporal, ChronoField.SECOND_OF_MINUTE);
		int nano = getChronoFieldOrDefault(temporal, ChronoField.NANO_OF_SECOND);
		return LocalTime.of(hour, minute, second, nano);
	}


	private static int getChronoFieldOrDefault(TemporalAccessor temporal, ChronoField field) {
		return (temporal.isSupported(field)) ?
			temporal.get(field) : (int) field.range().getMinimum();
	}

}

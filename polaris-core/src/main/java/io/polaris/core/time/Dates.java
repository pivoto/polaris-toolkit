package io.polaris.core.time;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("SpellCheckingInspection")
public class Dates implements DateConsts {
	/** 日期格式化器缓存 */
	private static final Map<String, DateTimeFormatter> formatterCache = new ConcurrentHashMap<>();
	/** yyyyMMdd格式化器 */
	public static final DateTimeFormatter YYYYMMDD = getFormatter(PATTERN_YYYYMMDD);
	/** yyyyMMddHHmmss格式化器 */
	public static final DateTimeFormatter YYYYMMDDHHMMSS = getFormatter(PATTERN_YYYYMMDDHHMMSS);
	/** yyyyMMddHHmmssSSS格式化器 */
	public static final DateTimeFormatter YYYYMMDDHHMMSSSSS = getFormatter(PATTERN_YYYYMMDDHHMMSSSSS);
	/** HHmmss格式化器 */
	public static final DateTimeFormatter HHMMSS = getFormatter(PATTERN_HHMMSS);
	/** HHmmssSSS格式化器 */
	public static final DateTimeFormatter HHMMSSSSS = getFormatter(PATTERN_HHMMSSSSS);
	/** yyyy-MM-dd格式化器 */
	public static final DateTimeFormatter YYYY_MM_DD = getFormatter(PATTERN_YYYY_MM_DD);
	/** yyyy-MM-dd HH:mm:ss格式化器 */
	public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = getFormatter(PATTERN_YYYY_MM_DD_HH_MM_SS);
	/** yyyy-MM-dd HH:mm:ss.SSS格式化器 */
	public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSS = getFormatter(PATTERN_YYYY_MM_DD_HH_MM_SS_SSS);
	/** HH:mm:ss格式化器 */
	public static final DateTimeFormatter HH_MM_SS = getFormatter(PATTERN_HH_MM_SS);
	/** HH:mm:ss.SSS格式化器 */
	public static final DateTimeFormatter HH_MM_SS_SSS = getFormatter(PATTERN_HH_MM_SS_SSS);

	/**
	 * 获取指定格式的日期格式化器
	 *
	 * @param formatterStr 格式化字符串
	 * @return 日期格式化器
	 */
	public static DateTimeFormatter getFormatter(String formatterStr) {
		return formatterCache.computeIfAbsent(formatterStr, key ->
			DateTimeFormatter.ofPattern(formatterStr).withZone(ZoneId.systemDefault())
		);
	}

	/**
	 * 获取当前时间戳
	 *
	 * @return 当前时间戳
	 */
	public static Instant now() {
		return Instant.now();
	}

	/**
	 * 获取当前时间字符串
	 *
	 * @return 当前时间字符串
	 */
	public static String nowStr() {
		return YYYY_MM_DD_HH_MM_SS_SSS.format(LocalDateTime.now());
	}

	/**
	 * 获取当前日期对象
	 *
	 * @return 当前日期对象
	 */
	public static Date nowDate() {
		return new Date();
	}

	/**
	 * 使用默认格式格式化LocalDateTime
	 *
	 * @param localDateTime LocalDateTime对象
	 * @return 格式化后的字符串
	 */
	public static String formatDefault(LocalDateTime localDateTime) {
		return YYYY_MM_DD_HH_MM_SS_SSS.format(localDateTime);
	}

	/**
	 * 使用默认格式格式化TemporalAccessor
	 *
	 * @param temporal TemporalAccessor对象
	 * @return 格式化后的字符串
	 */
	public static String formatDefault(TemporalAccessor temporal) {
		return YYYY_MM_DD_HH_MM_SS_SSS.format(toLocalDateTime(temporal));
	}

	/**
	 * 使用指定格式格式化TemporalAccessor
	 *
	 * @param format   格式化字符串
	 * @param temporal TemporalAccessor对象
	 * @return 格式化后的字符串
	 */
	public static String format(String format, TemporalAccessor temporal) {
		return getFormatter(format).format(temporal);
	}

	/**
	 * 使用默认格式格式化Date
	 *
	 * @param date Date对象
	 * @return 格式化后的字符串
	 */
	public static String formatDefault(Date date) {
		return YYYY_MM_DD_HH_MM_SS_SSS.format(toLocalDateTime(date));
	}

	/**
	 * 使用指定格式格式化Date
	 *
	 * @param format 格式化字符串
	 * @param date   Date对象
	 * @return 格式化后的字符串
	 */
	public static String format(String format, Date date) {
		return getFormatter(format).format(toLocalDateTime(date));
	}

	/**
	 * 解析字符串为TemporalAccessor对象
	 *
	 * @param format   格式化字符串
	 * @param temporal 日期时间字符串
	 * @return 解析后的TemporalAccessor对象
	 */
	public static TemporalAccessor parse(String format, String temporal) {
		return getFormatter(format).parse(temporal);
	}

	/**
	 * 解析字符串为Date对象
	 *
	 * @param format   格式化字符串
	 * @param temporal 日期时间字符串
	 * @return 解析后的Date对象
	 */
	public static Date parseDate(String format, String temporal) {
		return toDate(getFormatter(format).parse(temporal));
	}

	/**
	 * 解析字符串为LocalDateTime对象
	 *
	 * @param format   格式化字符串
	 * @param temporal 日期时间字符串
	 * @return 解析后的LocalDateTime对象
	 */
	public static LocalDateTime parseLocalDateTime(String format, String temporal) {
		return toLocalDateTime(getFormatter(format).parse(temporal));
	}

	/**
	 * 解析字符串为LocalDate对象
	 *
	 * @param format   格式化字符串
	 * @param temporal 日期时间字符串
	 * @return 解析后的LocalDate对象
	 */
	public static LocalDate parseLocalDate(String format, String temporal) {
		return toLocalDate(getFormatter(format).parse(temporal));
	}

	/**
	 * 解析字符串为LocalTime对象
	 *
	 * @param format   格式化字符串
	 * @param temporal 日期时间字符串
	 * @return 解析后的LocalTime对象
	 */
	public static LocalTime parseLocalTime(String format, String temporal) {
		return toLocalTime(getFormatter(format).parse(temporal));
	}

	/**
	 * 自动解析字符串为TemporalAccessor对象
	 *
	 * @param valueStr 日期时间字符串
	 * @return 解析后的TemporalAccessor对象
	 */
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

	/**
	 * 解析字符串为Date对象
	 *
	 * @param valueStr 日期时间字符串
	 * @return 解析后的Date对象
	 */
	public static Date parseDate(String valueStr) {
		return Dates.toDate(parse(valueStr));
	}

	/**
	 * 解析字符串为LocalDateTime对象
	 *
	 * @param valueStr 日期时间字符串
	 * @return 解析后的LocalDateTime对象
	 */
	public static LocalDateTime parseLocalDateTime(String valueStr) {
		return toLocalDateTime(parse(valueStr));
	}

	/**
	 * 解析字符串为LocalDate对象
	 *
	 * @param valueStr 日期时间字符串
	 * @return 解析后的LocalDate对象
	 */
	public static LocalDate parseLocalDate(String valueStr) {
		return toLocalDate(parse(valueStr));
	}

	/**
	 * 解析字符串为LocalTime对象
	 *
	 * @param valueStr 日期时间字符串
	 * @return 解析后的LocalTime对象
	 */
	public static LocalTime parseLocalTime(String valueStr) {
		return toLocalTime(parse(valueStr));
	}


	/**
	 * 将Date对象转换为毫秒数
	 *
	 * @param date Date对象
	 * @return 毫秒数
	 */
	public static long toMills(Date date) {
		return date.getTime();
	}

	/**
	 * 将LocalDateTime对象转换为毫秒数
	 *
	 * @param localDateTime LocalDateTime对象
	 * @return 毫秒数
	 */
	public static long toMills(LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	/**
	 * 将Instant对象转换为毫秒数
	 *
	 * @param instant Instant对象
	 * @return 毫秒数
	 */
	public static long toMills(Instant instant) {
		return instant.toEpochMilli();
	}


	/**
	 * 将TemporalAccessor对象转换为毫秒数
	 *
	 * @param temporal TemporalAccessor对象
	 * @return 毫秒数
	 */
	public static long toMills(TemporalAccessor temporal) {
		return toMills(toLocalDateTime(temporal));
	}


	/**
	 * 将毫秒数转换为Date对象
	 *
	 * @param mills 毫秒数
	 * @return Date对象
	 */
	public static Date toDate(long mills) {
		return new Date(mills);
	}

	/**
	 * 将Instant对象转换为Date对象
	 *
	 * @param instant Instant对象
	 * @return Date对象
	 */
	public static Date toDate(Instant instant) {
		return Date.from(instant);
	}

	/**
	 * 将LocalDateTime对象转换为Date对象
	 *
	 * @param localDateTime LocalDateTime对象
	 * @return Date对象
	 */
	public static Date toDate(LocalDateTime localDateTime) {
		return Date.from(toInstant(localDateTime));
	}

	/**
	 * 将LocalDate对象转换为Date对象
	 *
	 * @param localDate LocalDate对象
	 * @return Date对象
	 */
	public static Date toDate(LocalDate localDate) {
		return Date.from(toInstant(localDate));
	}


	/**
	 * 将TemporalAccessor对象转换为Date对象
	 *
	 * @param temporal TemporalAccessor对象
	 * @return Date对象
	 */
	public static Date toDate(TemporalAccessor temporal) {
		return toDate(toLocalDateTime(temporal));
	}


	/**
	 * 将毫秒数转换为Instant对象
	 *
	 * @param mills 毫秒数
	 * @return Instant对象
	 */
	public static Instant toInstant(long mills) {
		return Instant.ofEpochMilli(mills);
	}


	/**
	 * 将Date对象转换为Instant对象
	 *
	 * @param date Date对象
	 * @return Instant对象
	 */
	public static Instant toInstant(Date date) {
		return date.toInstant();
	}

	/**
	 * 将LocalDateTime对象转换为Instant对象
	 *
	 * @param localDateTime LocalDateTime对象
	 * @return Instant对象
	 */
	public static Instant toInstant(LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
	}

	/**
	 * 将LocalDate对象转换为Instant对象
	 *
	 * @param localDate LocalDate对象
	 * @return Instant对象
	 */
	public static Instant toInstant(LocalDate localDate) {
		return localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
	}

	/**
	 * 将TemporalAccessor对象转换为Instant对象
	 *
	 * @param temporal TemporalAccessor对象
	 * @return Instant对象
	 */
	public static Instant toInstant(TemporalAccessor temporal) {
		return toInstant(toLocalDateTime(temporal));
	}

	/**
	 * 将毫秒数转换为LocalDateTime对象
	 *
	 * @param mills 毫秒数
	 * @return LocalDateTime对象
	 */
	public static LocalDateTime toLocalDateTime(long mills) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(mills), ZoneId.systemDefault());
	}

	/**
	 * 将Date对象转换为LocalDateTime对象
	 *
	 * @param date Date对象
	 * @return LocalDateTime对象
	 */
	public static LocalDateTime toLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	/**
	 * 将TemporalAccessor对象转换为LocalDateTime对象
	 *
	 * @param temporal TemporalAccessor对象
	 * @return LocalDateTime对象
	 */
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


	/**
	 * 将毫秒数转换为LocalDate对象
	 *
	 * @param mills 毫秒数
	 * @return LocalDate对象
	 */
	public static LocalDate toLocalDate(long mills) {
		return toLocalDateTime(mills).toLocalDate();
	}

	/**
	 * 将Date对象转换为LocalDate对象
	 *
	 * @param date Date对象
	 * @return LocalDate对象
	 */
	public static LocalDate toLocalDate(Date date) {
		return toLocalDateTime(date).toLocalDate();
	}

	/**
	 * 将TemporalAccessor对象转换为LocalDate对象
	 *
	 * @param temporal TemporalAccessor对象
	 * @return LocalDate对象
	 */
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

	/**
	 * 将毫秒数转换为LocalTime对象
	 *
	 * @param mills 毫秒数
	 * @return LocalTime对象
	 */
	public static LocalTime toLocalTime(long mills) {
		return toLocalDateTime(mills).toLocalTime();
	}

	/**
	 * 将Date对象转换为LocalTime对象
	 *
	 * @param date Date对象
	 * @return LocalTime对象
	 */
	public static LocalTime toLocalTime(Date date) {
		return toLocalDateTime(date).toLocalTime();
	}

	/**
	 * 将TemporalAccessor对象转换为LocalTime对象
	 *
	 * @param temporal TemporalAccessor对象
	 * @return LocalTime对象
	 */
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


	/**
	 * 获取TemporalAccessor对象中指定字段的值，如果字段不支持则返回最小值
	 *
	 * @param temporal TemporalAccessor对象
	 * @param field    ChronoField字段
	 * @return 字段值
	 */
	private static int getChronoFieldOrDefault(TemporalAccessor temporal, ChronoField field) {
		return (temporal.isSupported(field)) ?
			temporal.get(field) : (int) field.range().getMinimum();
	}

	/**
	 * 计算两个日期之间的年数差
	 *
	 * @param t1 日期1
	 * @param t2 日期2
	 * @return 年数差
	 */
	public static int diffYears(Date t1, Date t2) {
		return diffYears(toLocalDateTime(t1), toLocalDateTime(t2));
	}

	/**
	 * 计算两个日期之间的月数差
	 *
	 * @param t1 日期1
	 * @param t2 日期2
	 * @return 月数差
	 */
	public static int diffMonths(Date t1, Date t2) {
		return diffMonths(toLocalDateTime(t1), toLocalDateTime(t2));
	}

	/**
	 * 计算两个日期之间的天数差
	 *
	 * @param t1 日期1
	 * @param t2 日期2
	 * @return 天数差
	 */
	public static long diffDays(Date t1, Date t2) {
		return diffDays(toLocalDateTime(t1), toLocalDateTime(t2));
	}


	/**
	 * 计算两个日期之间的小时数差
	 *
	 * @param t1 日期1
	 * @param t2 日期2
	 * @return 小时数差
	 */
	public static long diffHours(Date t1, Date t2) {
		return diffHours(toLocalDateTime(t1), toLocalDateTime(t2));
	}

	/**
	 * 计算两个日期之间的分钟数差
	 *
	 * @param t1 日期1
	 * @param t2 日期2
	 * @return 分钟数差
	 */
	public static long diffMinutes(Date t1, Date t2) {
		return diffMinutes(toLocalDateTime(t1), toLocalDateTime(t2));
	}

	/**
	 * 计算两个日期之间的秒数差
	 *
	 * @param t1 日期1
	 * @param t2 日期2
	 * @return 秒数差
	 */
	public static long diffSeconds(Date t1, Date t2) {
		return diffSeconds(toLocalDateTime(t1), toLocalDateTime(t2));
	}

	/**
	 * 计算两个日期之间的毫秒数差
	 *
	 * @param t1 日期1
	 * @param t2 日期2
	 * @return 毫秒数差
	 */
	public static long diffMillis(Date t1, Date t2) {
		return diffMillis(toLocalDateTime(t1), toLocalDateTime(t2));
	}

	/**
	 * 计算两个时间之间的年数差
	 *
	 * @param t1 时间1
	 * @param t2 时间2
	 * @return 年数差
	 */
	public static int diffYears(TemporalAccessor t1, TemporalAccessor t2) {
		Period period = Period.between(toLocalDate(t1), toLocalDate(t2));
		return period.getYears();
	}

	/**
	 * 计算两个时间之间的月数差
	 *
	 * @param t1 时间1
	 * @param t2 时间2
	 * @return 月数差
	 */
	public static int diffMonths(TemporalAccessor t1, TemporalAccessor t2) {
		Period period = Period.between(toLocalDate(t1), toLocalDate(t2));
		return period.getYears() * 12 + period.getMonths();
	}

	/**
	 * 计算两个时间之间的天数差
	 *
	 * @param t1 时间1
	 * @param t2 时间2
	 * @return 天数差
	 */
	public static long diffDays(TemporalAccessor t1, TemporalAccessor t2) {
		Duration duration = Duration.between(toLocalDateTime(t1), toLocalDateTime(t2));
		return duration.toDays();
	}

	/**
	 * 计算两个时间之间的小时数差
	 *
	 * @param t1 时间1
	 * @param t2 时间2
	 * @return 小时数差
	 */
	public static long diffHours(TemporalAccessor t1, TemporalAccessor t2) {
		Duration duration = Duration.between(toLocalDateTime(t1), toLocalDateTime(t2));
		return duration.toHours();
	}

	/**
	 * 计算两个时间之间的分钟数差
	 *
	 * @param t1 时间1
	 * @param t2 时间2
	 * @return 分钟数差
	 */
	public static long diffMinutes(TemporalAccessor t1, TemporalAccessor t2) {
		Duration duration = Duration.between(toLocalDateTime(t1), toLocalDateTime(t2));
		return duration.toMinutes();
	}

	/**
	 * 计算两个时间之间的秒数差
	 *
	 * @param t1 时间1
	 * @param t2 时间2
	 * @return 秒数差
	 */
	public static long diffSeconds(TemporalAccessor t1, TemporalAccessor t2) {
		Duration duration = Duration.between(toLocalDateTime(t1), toLocalDateTime(t2));
		return duration.toMillis() / 1000L;
	}

	/**
	 * 计算两个时间之间的毫秒数差
	 *
	 * @param t1 时间1
	 * @param t2 时间2
	 * @return 毫秒数差
	 */
	public static long diffMillis(TemporalAccessor t1, TemporalAccessor t2) {
		Duration duration = Duration.between(toLocalDateTime(t1), toLocalDateTime(t2));
		return duration.toMillis();
	}

	/**
	 * 计算两个时间之间的纳秒数差
	 *
	 * @param t1 时间1
	 * @param t2 时间2
	 * @return 纳秒数差
	 */
	public static long diffNanos(TemporalAccessor t1, TemporalAccessor t2) {
		Duration duration = Duration.between(toLocalDateTime(t1), toLocalDateTime(t2));
		return duration.toNanos();
	}


	/**
	 * 判断两个日期是否为同一天
	 *
	 * @param date1 日期1
	 * @param date2 日期2
	 * @return 是否为同一天
	 */
	public static boolean isSameDay(@Nonnull Date date1, @Nonnull Date date2) {
		return Calendars.isSameDay(Calendars.toCalendar(date1), Calendars.toCalendar(date2));
	}

	/**
	 * 判断两个日期是否为同一时刻
	 *
	 * @param date1 日期1
	 * @param date2 日期2
	 * @return 是否为同一时刻
	 */
	public static boolean isSameInstant(@Nonnull Date date1, @Nonnull Date date2) {
		return date1.getTime() == date2.getTime();
	}


	/**
	 * 获取指定字段的向上取整值
	 * <p>
	 * field 取值范围： Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND
	 *
	 * @param date  日期对象
	 * @param field 需要向上取整的字段
	 * @return 向上取整后的日历对象
	 */
	public static Date ceil(final Date date, final int field) {
		return Calendars.ceil(Calendars.toCalendar(date), field).getTime();
	}

	/**
	 * 获取指定字段的向上取整值
	 *
	 * @param date  日期对象
	 * @param field 需要向上取整的字段
	 * @return 向上取整后的日历对象
	 */
	public static Date ceil(final Date date, final CalStdField field) {
		return Calendars.ceil(Calendars.toCalendar(date), field).getTime();
	}

	/**
	 * 获取指定字段的向下取整值
	 * <p>
	 * field 取值范围： Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND
	 *
	 * @param date  日期对象
	 * @param field 需要向下取整的字段
	 * @return 向下取整后的日历对象
	 */
	public static Date floor(final Date date, final int field) {
		return Calendars.floor(Calendars.toCalendar(date), field).getTime();
	}


	/**
	 * 获取指定字段的向下取整值
	 *
	 * @param date  日期对象
	 * @param field 需要向下取整的字段
	 * @return 向下取整后的日历对象
	 */
	public static Date floor(final Date date, final CalStdField field) {
		return Calendars.floor(Calendars.toCalendar(date), field).getTime();
	}

	/**
	 * 获取指定字段的四舍五入值
	 * <p>
	 * field 取值范围： Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND
	 *
	 * @param date  日期对象
	 * @param field 需要四舍五入的字段
	 * @return 四舍五入后的日历对象
	 */
	public static Date round(final Date date, final int field) {
		return Calendars.round(Calendars.toCalendar(date), field).getTime();
	}

	/**
	 * 获取指定字段的四舍五入值
	 *
	 * @param date  日期对象
	 * @param field 需要四舍五入的字段
	 * @return 四舍五入后的日历对象
	 */
	public static Date round(final Date date, final CalStdField field) {
		return Calendars.round(Calendars.toCalendar(date), field).getTime();
	}

	/**
	 * 给指定日期增加年数
	 *
	 * @param date   日期对象
	 * @param amount 增加的年数
	 * @return 增加年数后的日期对象
	 */
	public static Date addYears(@Nonnull Date date, final int amount) {
		return Calendars.add(Calendars.toCalendar(date), Calendar.YEAR, amount).getTime();
	}

	/**
	 * 给指定日期增加月数
	 *
	 * @param date   日期对象
	 * @param amount 增加的月数
	 * @return 增加月数后的日期对象
	 */
	public static Date addMonths(@Nonnull Date date, final int amount) {
		return Calendars.add(Calendars.toCalendar(date), Calendar.MONTH, amount).getTime();
	}

	/**
	 * 给指定日期增加周数
	 *
	 * @param date   日期对象
	 * @param amount 增加的周数
	 * @return 增加周数后的日期对象
	 */
	public static Date addWeeks(@Nonnull Date date, final int amount) {
		return Calendars.add(Calendars.toCalendar(date), Calendar.WEEK_OF_YEAR, amount).getTime();
	}

	/**
	 * 给指定日期增加天数
	 *
	 * @param date   日期对象
	 * @param amount 增加的天数
	 * @return 增加天数后的日期对象
	 */
	public static Date addDays(@Nonnull Date date, final int amount) {
		return Calendars.add(Calendars.toCalendar(date), Calendar.DAY_OF_MONTH, amount).getTime();
	}

	/**
	 * 给指定日期增加小时数
	 *
	 * @param date   日期对象
	 * @param amount 增加的小时数
	 * @return 增加小时数后的日期对象
	 */
	public static Date addHours(@Nonnull Date date, final int amount) {
		return Calendars.add(Calendars.toCalendar(date), Calendar.HOUR_OF_DAY, amount).getTime();
	}

	/**
	 * 给指定日期增加分钟数
	 *
	 * @param date   日期对象
	 * @param amount 增加的分钟数
	 * @return 增加分钟数后的日期对象
	 */
	public static Date addMinutes(@Nonnull Date date, final int amount) {
		return Calendars.add(Calendars.toCalendar(date), Calendar.MINUTE, amount).getTime();
	}

	/**
	 * 给指定日期增加秒数
	 *
	 * @param date   日期对象
	 * @param amount 增加的秒数
	 * @return 增加秒数后的日期对象
	 */
	public static Date addSeconds(@Nonnull Date date, final int amount) {
		return Calendars.add(Calendars.toCalendar(date), Calendar.SECOND, amount).getTime();
	}

	/**
	 * 给指定日期增加毫秒数
	 *
	 * @param date   日期对象
	 * @param amount 增加的毫秒数
	 * @return 增加毫秒数后的日期对象
	 */
	public static Date addMilliseconds(@Nonnull Date date, final int amount) {
		return Calendars.add(Calendars.toCalendar(date), Calendar.MILLISECOND, amount).getTime();
	}

	/**
	 * 给指定日期增加指定字段的值
	 *
	 * @param date   日期对象
	 * @param field  日历字段
	 * @param amount 增加的数量
	 * @return 增加字段值后的日期对象
	 */
	public static Date add(@Nonnull Date date, CalAllField field, final int amount) {
		return Calendars.add(Calendars.toCalendar(date), field, amount).getTime();
	}


	/**
	 * 设置日期的年份
	 *
	 * @param date   日期对象
	 * @param amount 年份值
	 * @return 设置年份后的日期对象
	 */
	public static Date setYear(@Nonnull Date date, final int amount) {
		return Calendars.set(Calendars.toCalendar(date), Calendar.YEAR, amount).getTime();
	}

	/**
	 * 设置日期的月份
	 *
	 * @param date   日期对象
	 * @param amount 月份值
	 * @return 设置月份后的日期对象
	 */
	public static Date setMonth(@Nonnull Date date, final int amount) {
		return Calendars.set(Calendars.toCalendar(date), Calendar.MONTH, amount).getTime();
	}

	/**
	 * 设置日期的天数
	 *
	 * @param date   日期对象
	 * @param amount 天数值
	 * @return 设置天数后的日期对象
	 */
	public static Date setDay(@Nonnull Date date, final int amount) {
		return Calendars.set(Calendars.toCalendar(date), Calendar.DAY_OF_MONTH, amount).getTime();
	}

	/**
	 * 设置日期的小时数
	 *
	 * @param date   日期对象
	 * @param amount 小时值
	 * @return 设置小时数后的日期对象
	 */
	public static Date setHour(@Nonnull Date date, final int amount) {
		return Calendars.set(Calendars.toCalendar(date), Calendar.HOUR_OF_DAY, amount).getTime();
	}

	/**
	 * 设置日期的分钟数
	 *
	 * @param date   日期对象
	 * @param amount 分钟值
	 * @return 设置分钟数后的日期对象
	 */
	public static Date setMinute(@Nonnull Date date, final int amount) {
		return Calendars.set(Calendars.toCalendar(date), Calendar.MINUTE, amount).getTime();
	}

	/**
	 * 设置日期的秒数
	 *
	 * @param date   日期对象
	 * @param amount 秒数值
	 * @return 设置秒数后的日期对象
	 */
	public static Date setSecond(@Nonnull Date date, final int amount) {
		return Calendars.set(Calendars.toCalendar(date), Calendar.SECOND, amount).getTime();
	}

	/**
	 * 设置日期的毫秒数
	 *
	 * @param date   日期对象
	 * @param amount 毫秒值
	 * @return 设置毫秒数后的日期对象
	 */
	public static Date setMillisecond(@Nonnull Date date, final int amount) {
		return Calendars.set(Calendars.toCalendar(date), Calendar.MILLISECOND, amount).getTime();
	}

	/**
	 * 设置日期指定字段的值
	 *
	 * @param date   日期对象
	 * @param field  日历字段
	 * @param amount 字段值
	 * @return 设置字段值后的日期对象
	 */
	private static Date set(@Nonnull Date date, CalAllField field, final int amount) {
		return Calendars.set(Calendars.toCalendar(date), field, amount).getTime();
	}


	/**
	 * 获取日期的年份
	 *
	 * @param date 日期对象
	 * @return 年份值
	 */
	public static int getYear(@Nonnull Date date) {
		return Calendars.toCalendar(date).get(Calendar.YEAR);
	}

	/**
	 * 获取日期的月份
	 *
	 * @param date 日期对象
	 * @return 月份值
	 */
	public static int getMonth(@Nonnull Date date) {
		return Calendars.toCalendar(date).get(Calendar.MONTH);
	}

	/**
	 * 获取日期的天数
	 *
	 * @param date 日期对象
	 * @return 天数值
	 */
	public static int getDay(@Nonnull Date date) {
		return Calendars.toCalendar(date).get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取日期的小时数
	 *
	 * @param date 日期对象
	 * @return 小时值
	 */
	public static int getHour(@Nonnull Date date) {
		return Calendars.toCalendar(date).get(Calendar.HOUR_OF_DAY);
	}


	/**
	 * 获取日期的分钟数
	 *
	 * @param date 日期对象
	 * @return 分钟值
	 */
	public static int getMinute(@Nonnull Date date) {
		return Calendars.toCalendar(date).get(Calendar.MINUTE);
	}

	/**
	 * 获取日期的秒数
	 *
	 * @param date 日期对象
	 * @return 秒数值
	 */
	public static int getSecond(@Nonnull Date date) {
		return Calendars.toCalendar(date).get(Calendar.SECOND);
	}

	/**
	 * 获取日期的毫秒数
	 *
	 * @param date 日期对象
	 * @return 毫秒值
	 */
	public static int getMillisecond(@Nonnull Date date) {
		return Calendars.toCalendar(date).get(Calendar.MILLISECOND);
	}

	/**
	 * 获取日期指定字段的值
	 *
	 * @param date  日期对象
	 * @param field 日历字段
	 * @return 字段值
	 */
	public static int get(@Nonnull Date date, CalAllField field) {
		return Calendars.get(Calendars.toCalendar(date), field);
	}

}

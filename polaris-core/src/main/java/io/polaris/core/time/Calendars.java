package io.polaris.core.time;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Nonnull;

/**
 * @author Qt
 * @since May 30, 2024
 */
public class Calendars implements DateConsts {
	/**
	 * 格式化日期
	 *
	 * @param format   日期格式
	 * @param calendar 日历对象
	 * @return 格式化后的日期字符串
	 */
	public static String format(String format, Calendar calendar) {
		return Dates.format(format, calendar.getTime());
	}

	/**
	 * 使用默认格式格式化日期
	 *
	 * @param calendar 日历对象
	 * @return 格式化后的日期字符串
	 */
	public static String formatDefault(Calendar calendar) {
		return Dates.formatDefault(calendar.getTime());
	}

	/**
	 * 解析日期字符串为日历对象
	 *
	 * @param format   日期格式
	 * @param calendar 日期字符串
	 * @return 解析后的日历对象
	 */
	public static Calendar parse(@Nonnull String format, @Nonnull String calendar) {
		return toCalendar(Dates.parseDate(format, calendar));
	}

	/**
	 * 使用默认格式解析日期字符串为日历对象
	 *
	 * @param val 日期字符串
	 * @return 解析后的日历对象
	 */
	public static Calendar parse(@Nonnull String val) {
		return toCalendar(Dates.parseDate(val));
	}

	/**
	 * 将日期转换为日历对象
	 *
	 * @param date 日期对象
	 * @return 转换后的日历对象
	 */
	public static Calendar toCalendar(@Nonnull Date date) {
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	/**
	 * 将日期转换为指定时区的日历对象
	 *
	 * @param date 日期对象
	 * @param tz   时区
	 * @return 转换后的日历对象
	 */
	public static Calendar toCalendar(@Nonnull Date date, final TimeZone tz) {
		final Calendar c = Calendar.getInstance(tz);
		c.setTime(date);
		return c;
	}

	/**
	 * 判断两个日历对象是否表示同一天
	 *
	 * @param cal1 日历对象1
	 * @param cal2 日历对象2
	 * @return 是否为同一天
	 */
	public static boolean isSameDay(@Nonnull Calendar cal1, @Nonnull Calendar cal2) {
		return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
			cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
			cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 判断两个日期对象是否表示同一天
	 *
	 * @param date1 日期对象1
	 * @param date2 日期对象2
	 * @return 是否为同一天
	 */
	public static boolean isSameDay(@Nonnull Date date1, @Nonnull Date date2) {
		return isSameDay(toCalendar(date1), toCalendar(date2));
	}

	/**
	 * 判断两个日期对象是否表示同一时刻
	 *
	 * @param date1 日期对象1
	 * @param date2 日期对象2
	 * @return 是否为同一时刻
	 */
	public static boolean isSameInstant(@Nonnull Date date1, @Nonnull Date date2) {
		return date1.getTime() == date2.getTime();
	}

	/**
	 * 判断两个日历对象是否表示同一时刻
	 *
	 * @param cal1 日历对象1
	 * @param cal2 日历对象2
	 * @return 是否为同一时刻
	 */
	public static boolean isSameInstant(@Nonnull Calendar cal1, @Nonnull Calendar cal2) {
		return cal1.getTime().getTime() == cal2.getTime().getTime();
	}

	/**
	 * 获取指定字段的向上取整值
	 * <p>
	 * field 取值范围： Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND
	 *
	 * @param cal   日历对象
	 * @param field 需要向上取整的字段
	 * @return 向上取整后的日历对象
	 */
	public static Calendar ceil(final Calendar cal, final int field) {
		return upOrDown(cal, field, 1);
	}

	/**
	 * 获取指定字段的向上取整值
	 *
	 * @param cal   日历对象
	 * @param field 需要向上取整的字段枚举
	 * @return 向上取整后的日历对象
	 */
	public static Calendar ceil(final Calendar cal, final CalStdField field) {
		return upOrDown(cal, field.value(), 1);
	}

	/**
	 * 获取指定字段的向下取整值
	 * <p>
	 * field 取值范围： Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND
	 *
	 * @param cal   日历对象
	 * @param field 需要向下取整的字段
	 * @return 向下取整后的日历对象
	 */
	public static Calendar floor(final Calendar cal, final int field) {
		return upOrDown(cal, field, -1);
	}

	/**
	 * 获取指定字段的向下取整值
	 *
	 * @param cal   日历对象
	 * @param field 需要向下取整的字段
	 * @return 向下取整后的日历对象
	 */
	public static Calendar floor(final Calendar cal, final CalStdField field) {
		return upOrDown(cal, field.value(), -1);
	}

	/**
	 * 获取指定字段的四舍五入值
	 * <p>
	 * field 取值范围： Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND
	 *
	 * @param cal   日历对象
	 * @param field 需要四舍五入的字段
	 * @return 四舍五入后的日历对象
	 */
	public static Calendar round(final Calendar cal, final int field) {
		return upOrDown(cal, field, 0);
	}

	/**
	 * 获取指定字段的四舍五入值
	 *
	 * @param cal   日历对象
	 * @param field 需要四舍五入的字段
	 * @return 四舍五入后的日历对象
	 */
	public static Calendar round(final Calendar cal, final CalStdField field) {
		return upOrDown(cal, field.value(), 0);
	}

	private static Calendar upOrDown(final Calendar cal, final int field, int upOrDown) {
		boolean trim = false;
		boolean withHourOfDay = false;
		switch (field) {
			case Calendar.YEAR: {
				if (upOrDown == 0) {
					setRoundVal(cal, Calendar.YEAR, Calendar.ERA);
				} else if (upOrDown > 0) {
					setCeilVal(cal, new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}, Calendar.ERA);
				} else {
					floorFieldVal(cal, Calendar.YEAR);
				}
				trim = true;
			}
			case Calendar.MONTH: {
				if (trim) {
					cal.set(Calendar.MONTH, cal.getActualMinimum(Calendar.MONTH));
				} else {
					if (upOrDown == 0) {
						setRoundVal(cal, Calendar.MONTH, Calendar.YEAR);
					} else if (upOrDown > 0) {
						setCeilVal(cal, new int[]{Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}, Calendar.YEAR);
					} else {
						floorFieldVal(cal, Calendar.MONTH);
					}
					trim = true;
				}
			}
			case Calendar.DAY_OF_MONTH: {
				if (trim) {
					cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
				} else {
					if (upOrDown == 0) {
						setRoundVal(cal, Calendar.DAY_OF_MONTH, Calendar.MONTH);
					} else if (upOrDown > 0) {
						setCeilVal(cal, new int[]{Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}, Calendar.MONTH);
					} else {
						floorFieldVal(cal, Calendar.DAY_OF_MONTH);
					}
					trim = true;
				}
			}
			case Calendar.HOUR_OF_DAY: {
				if (trim) {
					cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
				} else {
					if (upOrDown == 0) {
						setRoundVal(cal, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH);
					} else if (upOrDown > 0) {
						setCeilVal(cal, new int[]{Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}, Calendar.DAY_OF_MONTH);
					} else {
						floorFieldVal(cal, Calendar.HOUR_OF_DAY);
					}
					trim = true;
				}
				withHourOfDay = true;
			}
			case Calendar.HOUR: {
				if (!withHourOfDay) {
					if (upOrDown == 0) {
						setRoundVal(cal, Calendar.HOUR, Calendar.AM_PM);
					} else if (upOrDown > 0) {
						setCeilVal(cal, new int[]{Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}, Calendar.AM_PM);
					} else {
						floorFieldVal(cal, Calendar.HOUR);
					}
					trim = true;
				}
			}
			case Calendar.MINUTE: {
				if (trim) {
					cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
				} else {
					if (upOrDown == 0) {
						setRoundVal(cal, Calendar.MINUTE, Calendar.HOUR_OF_DAY);
					} else if (upOrDown > 0) {
						setCeilVal(cal, new int[]{Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}, Calendar.HOUR_OF_DAY);
					} else {
						floorFieldVal(cal, Calendar.MINUTE);
					}
					trim = true;
				}
			}
			case Calendar.SECOND: {
				if (trim) {
					cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
				} else {
					if (upOrDown == 0) {
						setRoundVal(cal, Calendar.SECOND, Calendar.MINUTE);
					} else if (upOrDown > 0) {
						setCeilVal(cal, new int[]{Calendar.SECOND, Calendar.MILLISECOND}, Calendar.MINUTE);
					} else {
						floorFieldVal(cal, Calendar.SECOND);
					}
					trim = true;
				}
			}
			case Calendar.MILLISECOND: {
				if (trim) {
					cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
				} else {
					if (upOrDown == 0) {
						setRoundVal(cal, Calendar.MILLISECOND, Calendar.SECOND);
					} else if (upOrDown > 0) {
						setCeilVal(cal, new int[]{Calendar.MILLISECOND}, Calendar.SECOND);
					} else {
						floorFieldVal(cal, Calendar.MILLISECOND);
					}
					trim = true;
				}
			}
		}
		return cal;
	}

	/**
	 * 设置向上取整值
	 *
	 * @param cal     日历对象
	 * @param fields  需要处理的字段数组
	 * @param upField 上级字段
	 */
	@SuppressWarnings("MagicConstant")
	private static void setCeilVal(@Nonnull Calendar cal, int[] fields, int upField) {
		for (int field : fields) {
			int v = cal.get(field);
			int min = cal.getActualMinimum(field);
			if (v > min) {
				cal.set(field, min);
				if (upField > 0) {
					cal.add(upField, 1);
				}
				return;
			}
		}
	}

	/**
	 * 向下取整指定字段的值
	 *
	 * @param cal   日历对象
	 * @param field 需要向下取整的字段
	 */
	@SuppressWarnings("MagicConstant")
	private static void floorFieldVal(@Nonnull Calendar cal, int field) {
		int v = cal.get(field);
		int min = cal.getActualMinimum(field);
		if (v > min) {
			cal.set(field, min);
		}
	}

	/**
	 * 设置四舍五入值
	 *
	 * @param cal     日历对象
	 * @param field   需要四舍五入的字段
	 * @param upField 上级字段
	 */
	@SuppressWarnings("MagicConstant")
	private static void setRoundVal(@Nonnull Calendar cal, int field, int upField) {
		int v = cal.get(field);
		int min = cal.getActualMinimum(field);
		int max = cal.getActualMaximum(field);
		if (v - min >= max - v) {
			cal.set(field, min);
			if (upField > 0) {
				cal.add(upField, 1);
			}
		} else {
			cal.set(field, min);
		}
	}


	/**
	 * 给指定日期增加年数
	 *
	 * @param date   日期对象
	 * @param amount 增加的年数
	 * @return 增加年数后的日期对象
	 */
	public static Date addYears(@Nonnull Date date, final int amount) {
		return add(date, Calendar.YEAR, amount);
	}

	/**
	 * 给指定日期增加月数
	 *
	 * @param date   日期对象
	 * @param amount 增加的月数
	 * @return 增加月数后的日期对象
	 */
	public static Date addMonths(@Nonnull Date date, final int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	/**
	 * 给指定日期增加周数
	 *
	 * @param date   日期对象
	 * @param amount 增加的周数
	 * @return 增加周数后的日期对象
	 */
	public static Date addWeeks(@Nonnull Date date, final int amount) {
		return add(date, Calendar.WEEK_OF_YEAR, amount);
	}

	/**
	 * 给指定日期增加天数
	 *
	 * @param date   日期对象
	 * @param amount 增加的天数
	 * @return 增加天数后的日期对象
	 */
	public static Date addDays(@Nonnull Date date, final int amount) {
		return add(date, Calendar.DAY_OF_MONTH, amount);
	}

	/**
	 * 给指定日期增加小时数
	 *
	 * @param date   日期对象
	 * @param amount 增加的小时数
	 * @return 增加小时数后的日期对象
	 */
	public static Date addHours(@Nonnull Date date, final int amount) {
		return add(date, Calendar.HOUR_OF_DAY, amount);
	}

	/**
	 * 给指定日期增加分钟数
	 *
	 * @param date   日期对象
	 * @param amount 增加的分钟数
	 * @return 增加分钟数后的日期对象
	 */
	public static Date addMinutes(@Nonnull Date date, final int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	/**
	 * 给指定日期增加秒数
	 *
	 * @param date   日期对象
	 * @param amount 增加的秒数
	 * @return 增加秒数后的日期对象
	 */
	public static Date addSeconds(@Nonnull Date date, final int amount) {
		return add(date, Calendar.SECOND, amount);
	}

	/**
	 * 给指定日期增加毫秒数
	 *
	 * @param date   日期对象
	 * @param amount 增加的毫秒数
	 * @return 增加毫秒数后的日期对象
	 */
	public static Date addMilliseconds(@Nonnull Date date, final int amount) {
		return add(date, Calendar.MILLISECOND, amount);
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
		return add(toCalendar(date), field, amount).getTime();
	}

	/**
	 * 给指定日期增加指定字段的值
	 *
	 * @param date   日期对象
	 * @param field  日历字段
	 * @param amount 增加的数量
	 * @return 增加字段值后的日期对象
	 */
	public static Date add(@Nonnull Date date, int field, final int amount) {
		return add(toCalendar(date), field, amount).getTime();
	}

	/**
	 * 给指定日历增加年数
	 *
	 * @param calendar 日历对象
	 * @param amount   增加的年数
	 * @return 增加年数后的日历对象
	 */
	public static Calendar addYears(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.YEAR, amount);
	}

	/**
	 * 给指定日历增加月数
	 *
	 * @param calendar 日历对象
	 * @param amount   增加的月数
	 * @return 增加月数后的日历对象
	 */
	public static Calendar addMonths(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.MONTH, amount);
	}

	/**
	 * 给指定日历增加周数
	 *
	 * @param calendar 日历对象
	 * @param amount   增加的周数
	 * @return 增加周数后的日历对象
	 */
	public static Calendar addWeeks(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.WEEK_OF_YEAR, amount);
	}

	/**
	 * 给指定日历增加天数
	 *
	 * @param calendar 日历对象
	 * @param amount   增加的天数
	 * @return 增加天数后的日历对象
	 */
	public static Calendar addDays(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.DAY_OF_MONTH, amount);
	}

	/**
	 * 给指定日历增加小时数
	 *
	 * @param calendar 日历对象
	 * @param amount   增加的小时数
	 * @return 增加小时数后的日历对象
	 */
	public static Calendar addHours(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.HOUR_OF_DAY, amount);
	}

	/**
	 * 给指定日历增加分钟数
	 *
	 * @param calendar 日历对象
	 * @param amount   增加的分钟数
	 * @return 增加分钟数后的日历对象
	 */
	public static Calendar addMinutes(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.MINUTE, amount);
	}

	/**
	 * 给指定日历增加秒数
	 *
	 * @param calendar 日历对象
	 * @param amount   增加的秒数
	 * @return 增加秒数后的日历对象
	 */
	public static Calendar addSeconds(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.SECOND, amount);
	}

	/**
	 * 给指定日历增加毫秒数
	 *
	 * @param calendar 日历对象
	 * @param amount   增加的毫秒数
	 * @return 增加毫秒数后的日历对象
	 */
	public static Calendar addMilliseconds(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.MILLISECOND, amount);
	}


	/**
	 * 给指定日历增加指定字段的值
	 *
	 * @param calendar 日历对象
	 * @param field    日历字段
	 * @param amount   增加的数量
	 * @return 增加字段值后的日历对象
	 */
	public static Calendar add(@Nonnull Calendar calendar, CalAllField field, final int amount) {
		// noinspection MagicConstant
		calendar.add(field.value(), amount);
		return calendar;
	}

	/**
	 * 给指定日历增加指定字段的值
	 *
	 * @param calendar 日历对象
	 * @param field    日历字段
	 * @param amount   增加的数量
	 * @return 增加字段值后的日历对象
	 */
	public static Calendar add(@Nonnull Calendar calendar, int field, final int amount) {
		if (field >= 0 && field < Calendar.FIELD_COUNT) {
			//noinspection MagicConstant
			calendar.add(field, amount);
		}
		return calendar;
	}


	/**
	 * 设置日期的年份
	 *
	 * @param date   日期对象
	 * @param amount 年份值
	 * @return 设置年份后的日期对象
	 */
	public static Date setYear(@Nonnull Date date, final int amount) {
		return set(date, Calendar.YEAR, amount);
	}

	/**
	 * 设置日期的月份
	 *
	 * @param date   日期对象
	 * @param amount 月份值
	 * @return 设置月份后的日期对象
	 */
	public static Date setMonth(@Nonnull Date date, final int amount) {
		return set(date, Calendar.MONTH, amount);
	}

	/**
	 * 设置日期的天数
	 *
	 * @param date   日期对象
	 * @param amount 天数值
	 * @return 设置天数后的日期对象
	 */
	public static Date setDay(@Nonnull Date date, final int amount) {
		return set(date, Calendar.DAY_OF_MONTH, amount);
	}

	/**
	 * 设置日期的小时数
	 *
	 * @param date   日期对象
	 * @param amount 小时值
	 * @return 设置小时数后的日期对象
	 */
	public static Date setHour(@Nonnull Date date, final int amount) {
		return set(date, Calendar.HOUR_OF_DAY, amount);
	}

	/**
	 * 设置日期的分钟数
	 *
	 * @param date   日期对象
	 * @param amount 分钟值
	 * @return 设置分钟数后的日期对象
	 */
	public static Date setMinute(@Nonnull Date date, final int amount) {
		return set(date, Calendar.MINUTE, amount);
	}

	/**
	 * 设置日期的秒数
	 *
	 * @param date   日期对象
	 * @param amount 秒数值
	 * @return 设置秒数后的日期对象
	 */
	public static Date setSecond(@Nonnull Date date, final int amount) {
		return set(date, Calendar.SECOND, amount);
	}

	/**
	 * 设置日期的毫秒数
	 *
	 * @param date   日期对象
	 * @param amount 毫秒值
	 * @return 设置毫秒数后的日期对象
	 */
	public static Date setMillisecond(@Nonnull Date date, final int amount) {
		return set(date, Calendar.MILLISECOND, amount);
	}

	/**
	 * 设置日期指定字段的值
	 *
	 * @param date   日期对象
	 * @param field  日历字段
	 * @param amount 字段值
	 * @return 设置字段值后的日期对象
	 */
	private static Date set(@Nonnull Date date, int field, final int amount) {
		return set(toCalendar(date), field, amount).getTime();
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
		return set(toCalendar(date), field, amount).getTime();
	}

	/**
	 * 设置日历的年份
	 *
	 * @param calendar 日历对象
	 * @param amount   年份值
	 * @return 设置年份后的日历对象
	 */
	public static Calendar setYear(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.YEAR, amount);
	}

	/**
	 * 设置日历的月份
	 *
	 * @param calendar 日历对象
	 * @param amount   月份值
	 * @return 设置月份后的日历对象
	 */
	public static Calendar setMonth(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.MONTH, amount);
	}

	/**
	 * 设置日历的天数
	 *
	 * @param calendar 日历对象
	 * @param amount   天数值
	 * @return 设置天数后的日历对象
	 */
	public static Calendar setDay(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.DAY_OF_MONTH, amount);
	}

	/**
	 * 设置日历的小时数
	 *
	 * @param calendar 日历对象
	 * @param amount   小时值
	 * @return 设置小时数后的日历对象
	 */
	public static Calendar setHour(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.HOUR_OF_DAY, amount);
	}

	/**
	 * 设置日历的分钟数
	 *
	 * @param calendar 日历对象
	 * @param amount   分钟值
	 * @return 设置分钟数后的日历对象
	 */
	public static Calendar setMinute(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.MINUTE, amount);
	}

	/**
	 * 设置日历的秒数
	 *
	 * @param calendar 日历对象
	 * @param amount   秒数值
	 * @return 设置秒数后的日历对象
	 */
	public static Calendar setSecond(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.SECOND, amount);
	}

	/**
	 * 设置日历的毫秒数
	 *
	 * @param calendar 日历对象
	 * @param amount   毫秒值
	 * @return 设置毫秒数后的日历对象
	 */
	public static Calendar setMillisecond(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.MILLISECOND, amount);
	}

	/**
	 * 设置日历指定字段的值
	 *
	 * @param calendar 日历对象
	 * @param field    日历字段
	 * @param amount   字段值
	 * @return 设置字段值后的日历对象
	 */
	public static Calendar set(@Nonnull Calendar calendar, CalAllField field, final int amount) {
		//noinspection MagicConstant
		calendar.set(field.value(), amount);
		return calendar;
	}

	/**
	 * 设置日历指定字段的值
	 *
	 * @param calendar 日历对象
	 * @param field    日历字段
	 * @param amount   字段值
	 * @return 设置字段值后的日历对象
	 */
	public static Calendar set(@Nonnull Calendar calendar, int field, final int amount) {
		if (field >= 0 && field < Calendar.FIELD_COUNT) {
			//noinspection MagicConstant
			calendar.set(field, amount);
		}
		return calendar;
	}


	/**
	 * 获取日期的年份
	 *
	 * @param date 日期对象
	 * @return 年份值
	 */
	public static int getYear(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.YEAR);
	}

	/**
	 * 获取日期的月份
	 *
	 * @param date 日期对象
	 * @return 月份值
	 */
	public static int getMonth(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.MONTH);
	}

	/**
	 * 获取日期的天数
	 *
	 * @param date 日期对象
	 * @return 天数值
	 */
	public static int getDay(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取日期的小时数
	 *
	 * @param date 日期对象
	 * @return 小时值
	 */
	public static int getHour(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.HOUR_OF_DAY);
	}


	/**
	 * 获取日期的分钟数
	 *
	 * @param date 日期对象
	 * @return 分钟值
	 */
	public static int getMinute(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.MINUTE);
	}

	/**
	 * 获取日期的秒数
	 *
	 * @param date 日期对象
	 * @return 秒数值
	 */
	public static int getSecond(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.SECOND);
	}

	/**
	 * 获取日期的毫秒数
	 *
	 * @param date 日期对象
	 * @return 毫秒值
	 */
	public static int getMillisecond(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.MILLISECOND);
	}

	/**
	 * 获取日期指定字段的值
	 *
	 * @param date  日期对象
	 * @param field 日历字段
	 * @return 字段值
	 */
	public static int get(@Nonnull Date date, CalAllField field) {
		return get(toCalendar(date), field);
	}

	/**
	 * 获取日期指定字段的值
	 *
	 * @param date  日期对象
	 * @param field 日历字段
	 * @return 字段值
	 */
	public static int get(@Nonnull Date date, int field) {
		return get(toCalendar(date), field);
	}

	/**
	 * 获取日历的年份
	 *
	 * @param calendar 日历对象
	 * @return 年份值
	 */
	public static int getYear(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取日历的月份
	 *
	 * @param calendar 日历对象
	 * @return 月份值
	 */
	public static int getMonth(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.MONTH);
	}

	/**
	 * 获取日历的天数
	 *
	 * @param calendar 日历对象
	 * @return 天数值
	 */
	public static int getDay(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取日历的小时数
	 *
	 * @param calendar 日历对象
	 * @return 小时值
	 */
	public static int getHour(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}


	/**
	 * 获取日历的分钟数
	 *
	 * @param calendar 日历对象
	 * @return 分钟值
	 */
	public static int getMinute(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.MINUTE);
	}

	/**
	 * 获取日历的秒数
	 *
	 * @param calendar 日历对象
	 * @return 秒数值
	 */
	public static int getSecond(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.SECOND);
	}

	/**
	 * 获取日历的毫秒数
	 *
	 * @param calendar 日历对象
	 * @return 毫秒值
	 */
	public static int getMillisecond(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.MILLISECOND);
	}

	/**
	 * 获取日历指定字段的值
	 *
	 * @param calendar 日历对象
	 * @param field    日历字段
	 * @return 字段值
	 */
	public static int get(@Nonnull Calendar calendar, CalAllField field) {
		//noinspection MagicConstant
		return calendar.get(field.value());
	}


	/**
	 * 获取日历指定字段的值
	 *
	 * @param calendar 日历对象
	 * @param field    日历字段
	 * @return 字段值
	 */
	public static int get(@Nonnull Calendar calendar, int field) {
		if (field < 0 || field >= Calendar.FIELD_COUNT) {
			return 0;
		}
		//noinspection MagicConstant
		return calendar.get(field);
	}

}

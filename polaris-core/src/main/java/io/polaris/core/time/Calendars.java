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
	public static String format(String format, Calendar calendar) {
		return Dates.format(format, calendar.getTime());
	}

	public static String formatDefault(Calendar calendar) {
		return Dates.formatDefault(calendar.getTime());
	}

	public static Calendar parse(@Nonnull String format, @Nonnull String calendar) {
		return toCalendar(Dates.parseDate(format, calendar));
	}

	public static Calendar parse(@Nonnull String val) {
		return toCalendar(Dates.parseDate(val));
	}

	public static Calendar toCalendar(@Nonnull Date date) {
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	public static Calendar toCalendar(@Nonnull Date date, final TimeZone tz) {
		final Calendar c = Calendar.getInstance(tz);
		c.setTime(date);
		return c;
	}

	public static boolean isSameDay(@Nonnull Calendar cal1, @Nonnull Calendar cal2) {
		return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
			cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
			cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}

	public static boolean isSameDay(@Nonnull Date date1, @Nonnull Date date2) {
		return isSameDay(toCalendar(date1), toCalendar(date2));
	}

	public static boolean isSameInstant(@Nonnull Date date1, @Nonnull Date date2) {
		return date1.getTime() == date2.getTime();
	}

	public static boolean isSameInstant(@Nonnull Calendar cal1, @Nonnull Calendar cal2) {
		return cal1.getTime().getTime() == cal2.getTime().getTime();
	}

	public static Calendar ceil(final Calendar cal, final int field) {
		return upOrDown(cal, field, 1);
	}

	public static Calendar floor(final Calendar cal, final int field) {
		return upOrDown(cal, field, -1);
	}

	public static Calendar round(final Calendar cal, final int field) {
		return upOrDown(cal, field, 0);
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

	private static void floorFieldVal(@Nonnull Calendar cal, int field) {
		int v = cal.get(field);
		int min = cal.getActualMinimum(field);
		if (v > min) {
			cal.set(field, min);
		}
	}

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


	public static Date addYears(@Nonnull Date date, final int amount) {
		return add(date, Calendar.YEAR, amount);
	}

	public static Date addMonths(@Nonnull Date date, final int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	public static Date addWeeks(@Nonnull Date date, final int amount) {
		return add(date, Calendar.WEEK_OF_YEAR, amount);
	}

	public static Date addDays(@Nonnull Date date, final int amount) {
		return add(date, Calendar.DAY_OF_MONTH, amount);
	}

	public static Date addHours(@Nonnull Date date, final int amount) {
		return add(date, Calendar.HOUR_OF_DAY, amount);
	}

	public static Date addMinutes(@Nonnull Date date, final int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	public static Date addSeconds(@Nonnull Date date, final int amount) {
		return add(date, Calendar.SECOND, amount);
	}

	public static Date addMilliseconds(@Nonnull Date date, final int amount) {
		return add(date, Calendar.MILLISECOND, amount);
	}

	public static Date add(@Nonnull Date date, int calendarField, final int amount) {
		return add(toCalendar(date), calendarField, amount).getTime();
	}

	public static Calendar addYears(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.YEAR, amount);
	}

	public static Calendar addMonths(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.MONTH, amount);
	}

	public static Calendar addWeeks(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.WEEK_OF_YEAR, amount);
	}

	public static Calendar addDays(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.DAY_OF_MONTH, amount);
	}

	public static Calendar addHours(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.HOUR_OF_DAY, amount);
	}

	public static Calendar addMinutes(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.MINUTE, amount);
	}

	public static Calendar addSeconds(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.SECOND, amount);
	}

	public static Calendar addMilliseconds(@Nonnull Calendar calendar, final int amount) {
		return add(calendar, Calendar.MILLISECOND, amount);
	}


	public static Calendar add(@Nonnull Calendar calendar, int calendarField, final int amount) {
		calendar.add(calendarField, amount);
		return calendar;
	}


	public static Date setYear(@Nonnull Date date, final int amount) {
		return set(date, Calendar.YEAR, amount);
	}

	public static Date setMonth(@Nonnull Date date, final int amount) {
		return set(date, Calendar.MONTH, amount);
	}

	public static Date setDay(@Nonnull Date date, final int amount) {
		return set(date, Calendar.DAY_OF_MONTH, amount);
	}

	public static Date setHour(@Nonnull Date date, final int amount) {
		return set(date, Calendar.HOUR_OF_DAY, amount);
	}

	public static Date setMinute(@Nonnull Date date, final int amount) {
		return set(date, Calendar.MINUTE, amount);
	}

	public static Date setSecond(@Nonnull Date date, final int amount) {
		return set(date, Calendar.SECOND, amount);
	}

	public static Date setMillisecond(@Nonnull Date date, final int amount) {
		return set(date, Calendar.MILLISECOND, amount);
	}

	private static Date set(@Nonnull Date date, int calendarField, final int amount) {
		return set(toCalendar(date), calendarField, amount).getTime();
	}

	public static Calendar setYear(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.YEAR, amount);
	}

	public static Calendar setMonth(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.MONTH, amount);
	}

	public static Calendar setDay(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.DAY_OF_MONTH, amount);
	}

	public static Calendar setHour(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.HOUR_OF_DAY, amount);
	}

	public static Calendar setMinute(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.MINUTE, amount);
	}

	public static Calendar setSecond(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.SECOND, amount);
	}

	public static Calendar setMillisecond(@Nonnull Calendar calendar, final int amount) {
		return set(calendar, Calendar.MILLISECOND, amount);
	}

	public static Calendar set(@Nonnull Calendar calendar, int calendarField, final int amount) {
		calendar.set(calendarField, amount);
		return calendar;
	}


	public static int getYear(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.YEAR);
	}

	public static int getMonth(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.MONTH);
	}

	public static int getDay(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.DAY_OF_MONTH);
	}

	public static int getHour(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.HOUR_OF_DAY);
	}


	public static int getMinute(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.MINUTE);
	}

	public static int getSecond(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.SECOND);
	}

	public static int getMillisecond(@Nonnull Date date) {
		return toCalendar(date).get(Calendar.MILLISECOND);
	}

	public static int get(@Nonnull Date date, int calendarField) {
		return get(toCalendar(date), calendarField);
	}

	public static int getYear(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.YEAR);
	}

	public static int getMonth(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.MONTH);
	}

	public static int getDay(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static int getHour(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}


	public static int getMinute(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.MINUTE);
	}

	public static int getSecond(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.SECOND);
	}

	public static int getMillisecond(@Nonnull Calendar calendar) {
		return calendar.get(Calendar.MILLISECOND);
	}

	public static int get(@Nonnull Calendar calendar, int calendarField) {
		return calendar.get(calendarField);
	}

}

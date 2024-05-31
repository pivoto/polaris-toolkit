package io.polaris.core.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since Jan 19, 2024
 */
public class DateFormats implements DateConsts {
	private static ThreadLocal<Map<String, DateFormat>> LOCAL = ThreadLocal.withInitial(ConcurrentHashMap::new);

	public static DateFormat get(String pattern) {
		return LOCAL.get().computeIfAbsent(pattern, p -> new SimpleDateFormat(pattern));
	}

	public static String formatNow(String pattern) {
		return get(pattern).format(new Date());
	}


	public static String format(Date date, String pattern) {
		return get(pattern).format(date);
	}

	public static String format(Calendar calendar, String pattern) {
		return get(pattern).format(calendar.getTime());
	}

	public static String format(long millis, String pattern) {
		return get(pattern).format(new Date(millis));
	}

	public static String formatDefault(Date date) {
		return get(PATTERN_YYYY_MM_DD_HH_MM_SS_SSS).format(date);
	}

	public static String formatDefault(Calendar calendar) {
		return get(PATTERN_YYYY_MM_DD_HH_MM_SS_SSS).format(calendar.getTime());
	}

	public static String formatDefault(long millis) {
		return get(PATTERN_YYYY_MM_DD_HH_MM_SS_SSS).format(new Date(millis));
	}

	public static Date parse(String date, String pattern) throws ParseException {
		return get(pattern).parse(date);
	}

	public static Date parseDefault(String date) throws ParseException {
		return get(PATTERN_YYYY_MM_DD_HH_MM_SS_SSS).parse(date);
	}
}

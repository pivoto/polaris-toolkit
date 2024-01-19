package io.polaris.core.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since 1.8,  Jan 19, 2024
 */
public class DateFormats {
	private static ThreadLocal<Map<String, DateFormat>> LOCAL = ThreadLocal.withInitial(ConcurrentHashMap::new);

	public static DateFormat get(String pattern) {
		return LOCAL.get().computeIfAbsent(pattern, p -> new SimpleDateFormat(pattern));
	}

	public static String formatCurrentDate(String pattern) {
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


}

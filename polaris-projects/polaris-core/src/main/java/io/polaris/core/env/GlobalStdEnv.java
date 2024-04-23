package io.polaris.core.env;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public class GlobalStdEnv {
	private static final ILogger log = ILoggers.of(GlobalStdEnv.class);

	private static class Holder {
		private static final StdEnv env = StdEnv.newInstance();

		static {
			try {
				env.withCustomizer();
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public static StdEnv env() {
		return Holder.env;
	}

	public static Properties asProperties() {
		return Holder.env.asProperties();
	}

	public static Map<String, String> asMap() {
		return Holder.env.asMap();
	}

	public static Set<String> keys() {
		return Holder.env.keys();
	}


	public static void set(String key, String value) {
		Holder.env.set(key, value);
	}

	public static void remove(final String key) {
		Holder.env.remove(key);
	}

	public static void setAppEnv(Env normal) {
		Holder.env.setAppEnv(normal);
	}

	public static void addEnvFirst(final Env env) {
		Holder.env.addEnvFirst(env);
	}

	public static void addEnvLast(final Env env) {
		Holder.env.addEnvLast(env);
	}

	public static boolean addEnvBefore(final String name, final Env env) {
		return Holder.env.addEnvBefore(name, env);
	}

	public static boolean addEnvAfter(final String name, final Env env) {
		return Holder.env.addEnvAfter(name, env);
	}

	public static boolean replaceEnv(final String name, final Env env) {
		return Holder.env.replaceEnv(name, env);
	}

	public static boolean removeEnv(final String name) {
		return Holder.env.removeEnv(name);
	}

	public static void setDefaults(Env defaults) {
		Holder.env.setDefaults(defaults);
	}

	public static void setDefaults(String key, String value) {
		Holder.env.setDefaults(key, value);
	}


	public static String get(String key) {
		return Holder.env.get(key);
	}

	public static String get(String key, String defaultVal) {
		return Holder.env.get(key, defaultVal);
	}

	public static String getOrEmpty(final String key) {
		return Holder.env.getOrEmpty(key);
	}

	public static String getOrDefault(final String key, final String defaultVal) {
		return Holder.env.getOrDefault(key, defaultVal);
	}

	public static String getOrDefaultIfEmpty(final String key, final String defaultVal) {
		return Holder.env.getOrDefaultIfEmpty(key, defaultVal);
	}

	public static String getOrDefaultIfBlank(final String key, final String defaultVal) {
		return Holder.env.getOrDefaultIfBlank(key, defaultVal);
	}

	public static String[] getArray(final String key) {
		return Holder.env.getArray(key);
	}

	public static String[] getArray(final String key, final String[] defaultVal) {
		return Holder.env.getArray(key, defaultVal);
	}

	public static String resolveRef(String origin, Function<String, String> getter) {
		return Holder.env.resolveRef(origin, getter);
	}

	public static String resolveRef(String origin) {
		return Holder.env.resolveRef(origin);
	}

	public static String resolveRef(String origin, Map<String, String> map) {
		return Holder.env.resolveRef(origin, map);
	}

	public static String resolveRef(String origin, Properties env) {
		return Holder.env.resolveRef(origin, env);
	}

	public static boolean getBoolean(String key) {
		return Holder.env.getBoolean(key);
	}

	public static boolean getBoolean(String key, boolean defaultVal) {
		return Holder.env.getBoolean(key, defaultVal);
	}

	public static int getInt(String key) {
		return Holder.env.getInt(key);
	}

	public static int getInt(String key, int defaultVal) {
		return Holder.env.getInt(key, defaultVal);
	}

	public static long getLong(String key) {
		return Holder.env.getLong(key);
	}

	public static long getLong(String key, long defaultVal) {
		return Holder.env.getLong(key, defaultVal);
	}

	public static LocalDate getLocalDate(String key, String defaultVal) {
		return Holder.env.getLocalDate(key, defaultVal);
	}

	public static LocalDateTime getLocalDateTime(String key, String defaultVal) {
		return Holder.env.getLocalDateTime(key, defaultVal);
	}

	public static LocalTime getLocalTime(String key, String defaultVal) {
		return Holder.env.getLocalTime(key, defaultVal);
	}

	public static Date getDate(String key, String defaultVal) {
		return Holder.env.getDate(key, defaultVal);
	}

	public static Timestamp getDateTime(String key, String defaultVal) {
		return Holder.env.getDateTime(key, defaultVal);
	}

	public static Time getTime(String key, String defaultVal) {
		return Holder.env.getTime(key, defaultVal);
	}
}

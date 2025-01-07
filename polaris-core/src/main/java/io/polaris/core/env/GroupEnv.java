package io.polaris.core.env;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Apr 23, 2024
 */
public class GroupEnv implements Env {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

	private final String name;
	private final CopyOnWriteArrayList<Env> envList = new CopyOnWriteArrayList<>();
	private final ThreadLocal<Deque<String>> resolvedKeys = new ThreadLocal<>();
	private final ThreadLocal<Boolean> retrieved = new ThreadLocal<>();
	private Env runtime;

	private GroupEnv(String name, boolean mutable) {
		this.name = name;
		if (mutable) {
			runtime = Env.wrap(new Properties());
		}
	}

	public static GroupEnv newInstance() {
		return newInstance(null, false);
	}

	public static GroupEnv newInstance(String name) {
		return newInstance(name, false);
	}

	public static GroupEnv newInstance(boolean mutable) {
		return newInstance(null, mutable);
	}

	public static GroupEnv newInstance(String name, boolean mutable) {
		return new GroupEnv(name, mutable);
	}

	@Override
	public String name() {
		return name;
	}

	public void addEnvFirst(Env env) {
		synchronized (envList) {
			envList.remove(env);
			envList.add(0, env);
		}
	}

	public void addEnvLast(Env env) {
		synchronized (envList) {
			envList.remove(env);
			envList.add(env);
		}
	}

	public boolean removeEnv(String name) {
		synchronized (envList) {
			int size = envList.size();
			for (int i = 0; i < size; i++) {
				if (Objects.equals(envList.get(i).name(), name)) {
					envList.remove(i);
					return true;
				}
			}
		}
		return false;
	}

	public boolean replaceEnv(String name, Env env) {
		synchronized (envList) {
			int size = envList.size();
			for (int i = 0; i < size; i++) {
				if (Objects.equals(envList.get(i).name(), name)) {
					envList.set(i, env);
					return true;
				}
			}
		}
		return false;
	}

	public boolean addEnvBefore(String name, Env env) {
		synchronized (envList) {
			int size = envList.size();
			for (int i = 0; i < size; i++) {
				if (Objects.equals(envList.get(i).name(), name)) {
					envList.add(i, env);
					return true;
				}
			}
		}
		return false;
	}

	public boolean addEnvAfter(String name, Env env) {
		synchronized (envList) {
			int size = envList.size();
			for (int i = 0; i < size; i++) {
				if (Objects.equals(envList.get(i).name(), name)) {
					envList.add(i + 1, env);
					return true;
				}
			}
		}
		return false;
	}

	public void clearEnv() {
		synchronized (envList) {
			envList.clear();
		}
	}


	@Override
	public void set(String key, String value) {
		if (runtime != null) {
			runtime.set(key, value);
		}
	}

	@Override
	public String get(String key) {
		if (isResolvingKey(key)) {
			return null;
		}
		try {
			pushResolveKey(key);
			String val = null;
			if (runtime != null) {
				val = runtime.get(key);
			}
			if (val == null) {
				for (Env env : envList) {
					val = env.get(key);
					if (val != null) {
						break;
					}
				}
			}
			try {
				if (val != null) {
					val = resolveRef(val);
				}
			} catch (Exception ignored) {
			}
			return val;
		} finally {
			pollResolveKey(key);
		}
	}

	@Override
	public void remove(String key) {
		if (isResolvingKey(key)) {
			return;
		}
		try {
			pushResolveKey(key);
			if (runtime != null) {
				runtime.remove(key);
			}
			for (Env env : envList) {
				env.remove(key);
			}
		} finally {
			pollResolveKey(key);
		}
	}

	@Override
	public Set<String> keys() {
		if (Boolean.TRUE.equals(retrieved.get())) {
			return Collections.emptySet();
		}
		retrieved.set(true);
		try {
			Set<String> keys = new LinkedHashSet<>();
			if (runtime != null) {
				keys.addAll(runtime.keys());
			}
			for (Env env : envList) {
				Set<String> keySet = env.keys();
				keys.addAll(keySet);
			}
			return keys;
		} finally {
			retrieved.remove();
		}
	}


	private boolean isResolvingKey(String key) {
		Deque<String> queue = resolvedKeys.get();
		return queue != null && queue.contains(key);
	}

	private void pushResolveKey(String key) {
		Deque<String> queue = resolvedKeys.get();
		if (queue == null) {
			resolvedKeys.set(queue = new ArrayDeque<>());
		}
		queue.offerFirst(key);

	}

	private void pollResolveKey(String key) {
		Deque<String> queue = resolvedKeys.get();
		if (Objects.equals(queue.peekFirst(), key)) {
			queue.pollFirst();
		}
		if (queue.isEmpty()) {
			resolvedKeys.remove();
		}
	}

	@Override
	public String get(String key, String defaultVal) {
		String val = get(key);
		return val != null ? val : defaultVal;
	}

	public String getOrEmpty(String key) {
		String val = get(key);
		return val != null ? val : "";
	}

	public String getOrDefault(String key, String defaultVal) {
		String val = get(key);
		return val != null ? val : defaultVal;
	}

	public String getOrDefaultIfEmpty(String key, String defaultVal) {
		String val = get(key);
		return Strings.isNotEmpty(val) ? val : defaultVal;
	}

	public String getOrDefaultIfBlank(String key, String defaultVal) {
		String val = get(key);
		return Strings.isNotBlank(val) ? val : defaultVal;
	}

	public String resolveRef(String origin, Function<String, String> getter) {
		return Strings.resolvePlaceholders(origin, getter, false);
	}

	public String resolveRef(String origin) {
		return resolveRef(origin, this::get);
	}

	public String resolveRef(String origin, Map<String, String> map) {
		return resolveRef(origin, map::get);
	}

	public String resolveRef(String origin, Properties env) {
		return resolveRef(origin, env::getProperty);
	}


	private boolean isInvalidPropertyValue(String val) {
		return Strings.isBlank(val);
	}

	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defaultVal) {
		String val = get(key);
		return isInvalidPropertyValue(val) ? defaultVal : Boolean.parseBoolean(val);
	}

	public int getInt(String key) {
		return getInt(key, 0);
	}

	public int getInt(String key, int defaultVal) {
		try {
			String val = get(key);
			return isInvalidPropertyValue(val) ? defaultVal : Integer.parseInt(val);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public long getLong(String key) {
		return getLong(key, 0L);
	}

	public long getLong(String key, long defaultVal) {
		try {
			String val = get(key);
			return isInvalidPropertyValue(val) ? defaultVal : Long.parseLong(val);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public LocalDate getLocalDate(String key, String defaultVal) {
		String val = get(key, defaultVal);
		LocalDate localDate = LocalDate.parse(val, DATE_FORMATTER);
		return localDate;
	}

	public LocalDateTime getLocalDateTime(String key, String defaultVal) {
		String val = get(key, defaultVal);
		LocalDateTime localDate = LocalDateTime.parse(val, DATE_TIME_FORMATTER);
		return localDate;
	}

	public LocalTime getLocalTime(String key, String defaultVal) {
		String val = get(key, defaultVal);
		LocalTime localDate = LocalTime.parse(val, TIME_FORMATTER);
		return localDate;
	}

	public Date getDate(String key, String defaultVal) {
		LocalDate localDate = getLocalDate(key, defaultVal);
		Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	public Timestamp getDateTime(String key, String defaultVal) {
		LocalDateTime localDateTime = getLocalDateTime(key, defaultVal);
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Timestamp.from(instant);
	}

	public Time getTime(String key, String defaultVal) {
		LocalDateTime localDateTime = getLocalTime(key, defaultVal).atDate(LocalDate.now());
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return new Time(instant.toEpochMilli());
	}

	public String[] getArray(String key) {
		return getArray(key, null);
	}

	public String[] getArray(String key, String[] defaultVal) {
		String val = get(key);
		return isInvalidPropertyValue(val) ? defaultVal : val.split("[,|\r\n]+");
	}

}

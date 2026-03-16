package io.polaris.core.env;

import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import javax.annotation.Nullable;

import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.lang.bean.PropertyAccessor;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.StringCases;

/**
 * @author Qt
 * @since Apr 23, 2024
 */
public class GlobalStdEnv {
	private static final Logger log = Loggers.of(GlobalStdEnv.class);

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

	@Nullable
	public static <T> T bind(String prefix, T target) {
		return bind(GlobalStdEnv.env(), prefix, target);
	}

	@Nullable
	public static <T> T bind(String prefix, Class<T> clazz) {
		return bind(GlobalStdEnv.env(), prefix, clazz);
	}

	@Nullable
	public static <T> T bind(Env env, String prefix, Class<T> clazz) {
		return bind(env, prefix, Reflects.newInstanceIfPossible(clazz), clazz);
	}

	@Nullable
	@SuppressWarnings("all")
	public static <T> T bind(Env env, String prefix, T target) {
		return bind(env, prefix, target, (Class<T>) target.getClass());
	}

	@Nullable
	@SuppressWarnings("all")
	private static <T> T bind(Env env, String prefix, T target, Class<T> clazz) {
		if (target == null) {
			return target;
		}
		Map<String, PropertyAccessor> map = Beans.getIndexedPropertyAccessors(clazz);
		for (Map.Entry<String, PropertyAccessor> entry : map.entrySet()) {
			try {
				String key = entry.getKey();
				PropertyAccessor accessor = entry.getValue();
				if (accessor.hasSetter()) {
					Class c = Types.getClass(accessor.type());
					String kebabCase = StringCases.camelToKebabCase(key);
					String k = prefix.endsWith(".") ? (prefix + kebabCase) : (prefix + "." + kebabCase);
					Object val = null;
					if (Beans.isBeanClass(c)) {
						if (accessor.hasSetter()) {
							val = accessor.get(target);
						}
						if (val == null) {
							val = Reflects.newInstanceIfPossible(c);
						}
						val = bind(env, k, val, c);
					} else if (Map.class.isAssignableFrom(c) || Collection.class.isAssignableFrom(c)) {
						if (accessor.hasSetter()) {
							val = accessor.get(target);
						}
						if (val == null) {
							val = Reflects.newInstanceIfPossible(c);
						}
						if (val != null) {
							TreeMap<String, Object> treeMap = new TreeMap<>((k1, k2) -> {
								int ret = k1.length() - k2.length();
								if (ret != 0) {
									return ret;
								}
								return k1.compareTo(k2);
							});
							if (Map.class.isAssignableFrom(c)) {
								Type valueType = JavaType.of(accessor.type()).getActualType(Map.class, 1);
								env.keys().forEach((subKey) -> {
									if (subKey.startsWith(k + ".")) {
										Class valueClass = Types.getClass(valueType);
										int idx = subKey.indexOf('.', k.length() + 1);
										if (idx > k.length() + 1) {
											Object value = Reflects.newInstanceIfPossible(valueClass);
											if (value != null) {
												value = bind(env, subKey.substring(0, idx), value, valueClass);
												treeMap.put(subKey.substring(k.length() + 1, idx), value);
											}
										} else {
											Object v = bindSimpleValue(env, subKey, valueClass, valueType);
											treeMap.put(subKey.substring(k.length() + 1), v);
										}
									}
								});
								((Map) val).putAll(treeMap);
							} else {
								Type valueType = JavaType.of(accessor.type()).getActualType(Collection.class, 0);
								env.keys().forEach((subKey) -> {
									if (subKey.startsWith(k + ".")) {
										Class valueClass = Types.getClass(valueType);
										int idx = subKey.indexOf('.', k.length() + 1);
										if (idx > k.length() + 1) {
											Object value = Reflects.newInstanceIfPossible(valueClass);
											if (value != null) {
												value = bind(env, subKey.substring(0, idx), value, valueClass);
												treeMap.put(subKey.substring(k.length() + 1), value);
											}
										} else {
											Object v = bindSimpleValue(env, subKey, valueClass, valueType);
											treeMap.put(subKey.substring(k.length() + 1), v);
										}
									}
								});
								((Collection) val).addAll(treeMap.values());

							}
						}
					} else {
						val = bindSimpleValue(env, k, c, accessor.type());
					}
					if (val != null) {
						accessor.set(target, val);
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		return target;
	}

	@Nullable
	@SuppressWarnings("rawtypes")
	private static Object bindSimpleValue(Env env, String k, Class c, Type type) {
		Object val = null;
		if (c.isEnum()) {
			String s = env.get(k);
			for (Object o : c.getEnumConstants()) {
				if (((Enum<?>) o).name().equals(s)) {
					val = o;
				}
			}
			if (val == null) {
				for (Object o : c.getEnumConstants()) {
					if (((Enum<?>) o).name().equalsIgnoreCase(s)) {
						val = o;
					}
				}
			}
		} else {
			val = env.get(k);
			val = Converters.convertQuietly(type, val);
		}
		return val;
	}

	public static StdEnv env() {
		return Holder.env;
	}

	public static Env runtime() {
		return Holder.env.runtime();
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

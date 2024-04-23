package io.polaris.core.env;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.polaris.core.service.StatefulServiceLoader;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public class StdEnv implements Env {
	private static final Pattern pattern = Pattern.compile("\\$\\{([^{}]+)\\}");
	private static final ThreadLocal<Map<String, String>> resolvedKeysLocal = new ThreadLocal<>();
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	public static final String SYSTEM_PROPS = "SystemProps";
	public static final String SYSTEM_ENV = "SystemEnv";
	public static final String DEFAULT = "Default";
	public static final String APP_ENV = "AppEnv";

	private GroupEnv runtime = new GroupEnv(null, true);
	private DelegateEnv appEnv = new DelegateEnv(APP_ENV, null);
	private DelegateEnv defaults = new DelegateEnv(DEFAULT, null);
	private final AtomicBoolean customized = new AtomicBoolean(false);

	private StdEnv(String appConf) {
		runtime.addEnvLast(new SystemPropertiesWrapper(SYSTEM_PROPS));
		runtime.addEnvLast(new SystemEnvWrapper(SYSTEM_ENV));
		runtime.addEnvLast(appEnv);
		runtime.addEnvLast(defaults);
		if (appConf != null) {
			Env properties = loadProperties(APP_ENV, appConf);
			appEnv.setDelegate(properties);
		}
	}

	private Env loadProperties(String name, String appConf) {
		if (appConf != null) {
			GroupEnv group = new GroupEnv(name);
			try {
				try (FileInputStream fis = new FileInputStream(appConf);) {
					Properties properties = new Properties();
					properties.load(fis);
					if (!properties.isEmpty()) {
						group.addEnvLast(Env.wrap(properties));
					}
				} catch (IOException ignore) {
				}

				Properties propInDir = null;
				Properties propInJar = null;
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				Enumeration<URL> urls = classLoader.getResources(appConf);
				while (urls.hasMoreElements()) {
					URL url = urls.nextElement();
					Properties prop;
					if ("file".equals(url.getProtocol())) {
						if (propInDir == null) {
							propInDir = new Properties();
						}
						prop = propInDir;
					} else {
						if (propInJar == null) {
							propInJar = new Properties();
						}
						prop = propInJar;
					}
					try (InputStream in = url.openStream();) {
						Properties properties = new Properties();
						properties.load(in);
						if (!properties.isEmpty()) {
							for (String key : properties.stringPropertyNames()) {
								prop.putIfAbsent(key, properties.get(key));
							}
						}
					} catch (IOException ignore) {
					}
				}

				if (propInDir != null) {
					group.addEnvLast(Env.wrap(propInDir));
				}
				if (propInJar != null) {
					group.addEnvLast(Env.wrap(propInJar));
				}
			} catch (IOException ignore) {
			}
			return group;
		}
		return null;
	}

	public void setAppEnv(Env appEnv) {
		runtime.replaceEnv(APP_ENV, appEnv);
	}

	public static StdEnv newInstance() {
		return new StdEnv(null);
	}

	public static StdEnv newInstance(String conf) {
		return new StdEnv(conf);
	}

	public StdEnv withCustomizer() {
		if (customized.compareAndSet(false, true)) {
			try {
				StatefulServiceLoader<StdEnvCustomizer> loader = StatefulServiceLoader.load(StdEnvCustomizer.class);
				for (StdEnvCustomizer customizer : loader) {
					customizer.customize(this);
				}
			} catch (Throwable e) {
				customized.set(false);
				throw e;
			}
		}
		return this;
	}


	@Override
	public Set<String> keys() {
		return runtime.keys();
	}

	@Override
	public void set(String key, String value) {
		runtime.set(key, value);
	}

	@Override
	public void remove(final String key) {
		runtime.remove(key);
	}

	public void setDefaults(Env defaults) {
		this.defaults.setDelegate(defaults);
	}

	public void setDefaults(String key, String value) {
		if (defaults.getDelegate() == null) {
			defaults.setDelegate(Env.wrap(new Properties()));
		}
		defaults.set(key, value);
	}

	public void addEnvFirst(Env env) {
		runtime.addEnvFirst(env);
	}

	public void addEnvLast(Env env) {
		runtime.addEnvBefore(DEFAULT, env);
	}

	public boolean addEnvBefore(String name, Env env) {
		return runtime.addEnvBefore(name, env);
	}

	public boolean addEnvAfter(String name, Env env) {
		return runtime.addEnvAfter(name, env);
	}

	public boolean replaceEnv(String name, Env env) {
		return runtime.replaceEnv(name, env);
	}

	public boolean removeEnv(String name) {
		return runtime.removeEnv(name);
	}

	@Override
	public String get(String key) {
		String val = runtime.get(key);
		try {
			if (val != null) {
				val = resolveRef(val);
			}
		} catch (Exception ignored) {
		}
		return val;
	}

	private boolean isInvalidPropertyValue(String val) {
		return Strings.isBlank(val);
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
		if (origin == null) {
			return origin;
		}
		boolean hasInit = false;
		Map<String, String> resovedKeys = resolvedKeysLocal.get();
		if (resovedKeys == null) {
			hasInit = true;
			resolvedKeysLocal.set(resovedKeys = new HashMap<>());
		}
		try {
			Matcher matcher = pattern.matcher(origin);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String[] arr = matcher.group(1).split(":-", 2);
				String k = arr[0].trim();
				String defVal = arr.length > 1 ? arr[1].trim() : "";
				String v = null;
				if (resovedKeys.containsKey(k)) {
					v = resovedKeys.get(k);
				} else {
					v = getter.apply(k);
					resovedKeys.put(k, v);
				}
				if (v == null) {
					v = defVal;
				}
				v = v.replace("$", "\\$").replace("\\", "\\\\");
				matcher.appendReplacement(sb, v);
			}
			matcher.appendTail(sb);
			String result = sb.toString();
			if (pattern.matcher(result).find()) {
				return resolveRef(result, getter);
			}
			return result;
		} finally {
			if (hasInit) {
				resolvedKeysLocal.remove();
			}
		}
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

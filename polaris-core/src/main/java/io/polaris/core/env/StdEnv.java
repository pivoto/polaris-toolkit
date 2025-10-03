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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;

import io.polaris.core.service.StatefulServiceLoader;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Apr 23, 2024
 */
public class StdEnv implements Env {
	public static final String SYSTEM_PROPS = "SystemProps";
	public static final String SYSTEM_ENV = "SystemEnv";
	public static final String DEFAULT = "Default";
	public static final String APP_ENV = "AppEnv";

	private final String name;
	private final GroupEnv runtime;
	private final DelegateEnv defaults;
	private final AtomicBoolean customized = new AtomicBoolean(false);

	private StdEnv(String name, String appConf) {
		this.name = name;
		this.runtime = GroupEnv.newInstance(null, true);
		this.defaults = new DelegateEnv(DEFAULT, null);

		this.runtime.addEnvLast(new SystemPropertiesWrapper(SYSTEM_PROPS));
		this.runtime.addEnvLast(new SystemEnvWrapper(SYSTEM_ENV));
		DelegateEnv appEnv = new DelegateEnv(APP_ENV, null);
		this.runtime.addEnvLast(appEnv);
		if (appConf != null) {
			Env properties = Env.file(APP_ENV, appConf);
			appEnv.setDelegate(properties);
		}
		this.runtime.addEnvLast(defaults);
	}

	StdEnv(String name, GroupEnv runtime, DelegateEnv defaults) {
		this.name = name;
		this.runtime = runtime;
		this.defaults = defaults;
	}

	public static StdEnv newInstance() {
		return newInstance(null, null);
	}

	public static StdEnv newInstance(String conf) {
		return newInstance(null, conf);
	}

	public static StdEnv newInstance(String name, String conf) {
		return new StdEnv(name, conf);
	}

	public StdEnv shade(Predicate<Env> filter) {
		return new ShadeStdEnv(name, runtime, defaults, filter);
	}

	@Override
	public String name() {
		return this.name;
	}

	public void setAppEnv(Env appEnv) {
		this.runtime.replaceEnv(APP_ENV, appEnv);
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
		return this.runtime.keys();
	}

	@Override
	public void set(String key, String value) {
		this.runtime.set(key, value);
	}

	@Override
	public void remove(final String key) {
		this.runtime.remove(key);
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
		this.runtime.addEnvFirst(env);
	}

	public void addEnvLast(Env env) {
		this.runtime.addEnvBefore(DEFAULT, env);
	}

	public boolean addEnvBefore(String name, Env env) {
		return this.runtime.addEnvBefore(name, env);
	}

	public boolean addEnvAfter(String name, Env env) {
		return this.runtime.addEnvAfter(name, env);
	}

	public boolean replaceEnv(String name, Env env) {
		return this.runtime.replaceEnv(name, env);
	}

	public boolean removeEnv(String name) {
		return this.runtime.removeEnv(name);
	}

	@Override
	public String get(String key) {
		return this.runtime.get(key);
	}

	@Override
	public String get(String key, String defaultVal) {
		return this.runtime.get(key, defaultVal);
	}

	public String getOrEmpty(String key) {
		return this.runtime.getOrEmpty(key);
	}

	public String getOrDefault(String key, String defaultVal) {
		return this.runtime.getOrDefault(key, defaultVal);
	}

	public String getOrDefaultIfEmpty(String key, String defaultVal) {
		return this.runtime.getOrDefaultIfEmpty(key, defaultVal);
	}

	public String getOrDefaultIfBlank(String key, String defaultVal) {
		String val = get(key);
		return Strings.isNotBlank(val) ? val : defaultVal;
	}

	public String resolveRef(String origin, Function<String, String> getter) {
		return this.runtime.resolveRef(origin, getter);
	}

	public String resolveRef(String origin) {
		return this.runtime.resolveRef(origin);
	}

	public String resolveRef(String origin, Map<String, String> map) {
		return this.runtime.resolveRef(origin, map);
	}

	public String resolveRef(String origin, Properties env) {
		return this.runtime.resolveRef(origin, env);
	}

	public boolean getBoolean(String key) {
		return this.runtime.getBoolean(key);
	}

	public boolean getBoolean(String key, boolean defaultVal) {
		return this.runtime.getBoolean(key, defaultVal);
	}

	public int getInt(String key) {
		return this.runtime.getInt(key);
	}

	public int getInt(String key, int defaultVal) {
		return this.runtime.getInt(key, defaultVal);
	}

	public long getLong(String key) {
		return this.runtime.getLong(key);
	}

	public long getLong(String key, long defaultVal) {
		return this.runtime.getLong(key, defaultVal);
	}

	public LocalDate getLocalDate(String key, String defaultVal) {
		return this.runtime.getLocalDate(key, defaultVal);
	}

	public LocalDateTime getLocalDateTime(String key, String defaultVal) {
		return this.runtime.getLocalDateTime(key, defaultVal);
	}

	public LocalTime getLocalTime(String key, String defaultVal) {
		return this.runtime.getLocalTime(key, defaultVal);
	}

	public Date getDate(String key, String defaultVal) {
		return this.runtime.getDate(key, defaultVal);
	}

	public Timestamp getDateTime(String key, String defaultVal) {
		return this.runtime.getDateTime(key, defaultVal);
	}

	public Time getTime(String key, String defaultVal) {
		return this.runtime.getTime(key, defaultVal);
	}

	public String[] getArray(String key) {
		return this.runtime.getArray(key);
	}

	public String[] getArray(String key, String[] defaultVal) {
		return this.runtime.getArray(key, defaultVal);
	}

}

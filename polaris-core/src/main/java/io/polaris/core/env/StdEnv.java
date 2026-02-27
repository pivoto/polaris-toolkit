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

import io.polaris.core.service.SpiLoaders;
import io.polaris.core.service.StatefulServiceLoader;
import io.polaris.core.string.Strings;

/**
 * 标准环境实现类，用于管理应用程序的配置环境。
 * 支持系统属性、系统环境变量、应用配置文件和默认值的分层管理。
 *
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

	/**
	 * 私有构造函数，用于创建标准环境实例。
	 *
	 * @param name    环境名称
	 * @param appConf 应用配置文件路径
	 */
	private StdEnv(String name, String appConf) {
		this.name = name;
		this.runtime = GroupEnv.newInstance(null, true);
		this.defaults = new DelegateEnv(DEFAULT, null);

		// 添加系统属性环境
		this.runtime.addEnvLast(new SystemPropertiesWrapper(SYSTEM_PROPS));
		// 添加系统环境变量环境
		this.runtime.addEnvLast(new SystemEnvWrapper(SYSTEM_ENV));

		// 添加应用环境
		DelegateEnv appEnv = new DelegateEnv(APP_ENV, null);
		this.runtime.addEnvLast(appEnv);
		if (appConf != null) {
			Env properties = Env.file(APP_ENV, appConf);
			appEnv.setDelegate(properties);
		}
		// 添加默认环境
		this.runtime.addEnvLast(defaults);
	}

	/**
	 * 包级私有构造函数，用于创建标准环境实例。
	 *
	 * @param name     环境名称
	 * @param runtime  运行时环境组
	 * @param defaults 默认环境
	 */
	StdEnv(String name, GroupEnv runtime, DelegateEnv defaults) {
		this.name = name;
		this.runtime = runtime;
		this.defaults = defaults;
	}

	/**
	 * 创建新的标准环境实例。
	 *
	 * @return 新的标准环境实例
	 */
	public static StdEnv newInstance() {
		return newInstance(null, null);
	}

	/**
	 * 创建新的标准环境实例。
	 *
	 * @param conf 配置文件路径
	 * @return 新的标准环境实例
	 */
	public static StdEnv newInstance(String conf) {
		return newInstance(null, conf);
	}

	/**
	 * 创建新的标准环境实例。
	 *
	 * @param name 环境名称
	 * @param conf 配置文件路径
	 * @return 新的标准环境实例
	 */
	public static StdEnv newInstance(String name, String conf) {
		return new StdEnv(name, conf);
	}

	/**
	 * 创建一个遮罩环境对象，根据提供的过滤器决定哪些环境变量可见。
	 *
	 * @param filter 过滤器，用于判断环境是否应该被包含在遮蔽环境中
	 * @return 新的遮罩环境实例
	 */
	public StdEnv shade(Predicate<Env> filter) {
		return new ShadeStdEnv(name, runtime, defaults, filter);
	}

	@Override
	public String name() {
		return this.name;
	}

	/**
	 * 设置应用环境。
	 *
	 * @param appEnv 应用环境
	 */
	public void setAppEnv(Env appEnv) {
		this.runtime.replaceEnv(APP_ENV, appEnv);
	}

	/**
	 * 应用自定义配置。
	 *
	 * @return 当前环境实例
	 */
	public StdEnv withCustomizer() {
		if (customized.compareAndSet(false, true)) {
			try {
				StatefulServiceLoader<StdEnvCustomizer> loader = SpiLoaders.loadStateful(StdEnvCustomizer.class);
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

	/**
	 * 设置默认环境。
	 *
	 * @param defaults 默认环境
	 */
	public void setDefaults(Env defaults) {
		this.defaults.setDelegate(defaults);
	}

	/**
	 * 设置默认值。
	 *
	 * @param key   键
	 * @param value 值
	 */
	public void setDefaults(String key, String value) {
		if (defaults.getDelegate() == null) {
			defaults.setDelegate(Env.wrap(new Properties()));
		}
		defaults.set(key, value);
	}

	/**
	 * 在环境链开头添加环境。
	 *
	 * @param env 环境
	 */
	public void addEnvFirst(Env env) {
		this.runtime.addEnvFirst(env);
	}

	/**
	 * 在默认环境之前添加环境。
	 *
	 * @param env 环境
	 */
	public void addEnvLast(Env env) {
		this.runtime.addEnvBefore(DEFAULT, env);
	}

	/**
	 * 在指定环境之前添加环境。
	 *
	 * @param name 环境名称
	 * @param env  环境
	 * @return 是否添加成功
	 */
	public boolean addEnvBefore(String name, Env env) {
		return this.runtime.addEnvBefore(name, env);
	}

	/**
	 * 在指定环境之后添加环境。
	 *
	 * @param name 环境名称
	 * @param env  环境
	 * @return 是否添加成功
	 */
	public boolean addEnvAfter(String name, Env env) {
		return this.runtime.addEnvAfter(name, env);
	}

	/**
	 * 替换指定环境。
	 *
	 * @param name 环境名称
	 * @param env  环境
	 * @return 是否替换成功
	 */
	public boolean replaceEnv(String name, Env env) {
		return this.runtime.replaceEnv(name, env);
	}

	/**
	 * 移除指定环境。
	 *
	 * @param name 环境名称
	 * @return 是否移除成功
	 */
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

	/**
	 * 获取键对应的值，如果不存在则返回空字符串。
	 *
	 * @param key 键
	 * @return 值或空字符串
	 */
	public String getOrEmpty(String key) {
		return this.runtime.getOrEmpty(key);
	}

	/**
	 * 获取键对应的值，如果不存在则返回默认值。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 值或默认值
	 */
	public String getOrDefault(String key, String defaultVal) {
		return this.runtime.getOrDefault(key, defaultVal);
	}

	/**
	 * 获取键对应的值，如果为空则返回默认值。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 值或默认值
	 */
	public String getOrDefaultIfEmpty(String key, String defaultVal) {
		return this.runtime.getOrDefaultIfEmpty(key, defaultVal);
	}

	/**
	 * 获取键对应的值，如果为空白则返回默认值。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 值或默认值
	 */
	public String getOrDefaultIfBlank(String key, String defaultVal) {
		String val = get(key);
		return Strings.isNotBlank(val) ? val : defaultVal;
	}

	/**
	 * 解析引用字符串。
	 *
	 * @param origin 原始字符串
	 * @param getter 获取器函数
	 * @return 解析后的字符串
	 */
	public String resolveRef(String origin, Function<String, String> getter) {
		return this.runtime.resolveRef(origin, getter);
	}

	/**
	 * 解析引用字符串。
	 *
	 * @param origin 原始字符串
	 * @return 解析后的字符串
	 */
	public String resolveRef(String origin) {
		return this.runtime.resolveRef(origin);
	}

	/**
	 * 解析引用字符串。
	 *
	 * @param origin 原始字符串
	 * @param map    映射表
	 * @return 解析后的字符串
	 */
	public String resolveRef(String origin, Map<String, String> map) {
		return this.runtime.resolveRef(origin, map);
	}

	/**
	 * 解析引用字符串。
	 *
	 * @param origin 原始字符串
	 * @param env    环境属性
	 * @return 解析后的字符串
	 */
	public String resolveRef(String origin, Properties env) {
		return this.runtime.resolveRef(origin, env);
	}

	/**
	 * 获取布尔值。
	 *
	 * @param key 键
	 * @return 布尔值
	 */
	public boolean getBoolean(String key) {
		return this.runtime.getBoolean(key);
	}

	/**
	 * 获取布尔值。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 布尔值
	 */
	public boolean getBoolean(String key, boolean defaultVal) {
		return this.runtime.getBoolean(key, defaultVal);
	}

	/**
	 * 获取整数值。
	 *
	 * @param key 键
	 * @return 整数值
	 */
	public int getInt(String key) {
		return this.runtime.getInt(key);
	}

	/**
	 * 获取整数值。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 整数值
	 */
	public int getInt(String key, int defaultVal) {
		return this.runtime.getInt(key, defaultVal);
	}

	/**
	 * 获取长整数值。
	 *
	 * @param key 键
	 * @return 长整数值
	 */
	public long getLong(String key) {
		return this.runtime.getLong(key);
	}

	/**
	 * 获取长整数值。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 长整数值
	 */
	public long getLong(String key, long defaultVal) {
		return this.runtime.getLong(key, defaultVal);
	}

	/**
	 * 获取本地日期。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 本地日期
	 */
	public LocalDate getLocalDate(String key, String defaultVal) {
		return this.runtime.getLocalDate(key, defaultVal);
	}

	/**
	 * 获取本地日期时间。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 本地日期时间
	 */
	public LocalDateTime getLocalDateTime(String key, String defaultVal) {
		return this.runtime.getLocalDateTime(key, defaultVal);
	}

	/**
	 * 获取本地时间。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 本地时间
	 */
	public LocalTime getLocalTime(String key, String defaultVal) {
		return this.runtime.getLocalTime(key, defaultVal);
	}

	/**
	 * 获取日期。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 日期
	 */
	public Date getDate(String key, String defaultVal) {
		return this.runtime.getDate(key, defaultVal);
	}

	/**
	 * 获取日期时间。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 日期时间
	 */
	public Timestamp getDateTime(String key, String defaultVal) {
		return this.runtime.getDateTime(key, defaultVal);
	}

	/**
	 * 获取时间。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 时间
	 */
	public Time getTime(String key, String defaultVal) {
		return this.runtime.getTime(key, defaultVal);
	}

	/**
	 * 获取数组。
	 *
	 * @param key 键
	 * @return 数组
	 */
	public String[] getArray(String key) {
		return this.runtime.getArray(key);
	}

	/**
	 * 获取数组。
	 *
	 * @param key        键
	 * @param defaultVal 默认值
	 * @return 数组
	 */
	public String[] getArray(String key, String[] defaultVal) {
		return this.runtime.getArray(key, defaultVal);
	}
}

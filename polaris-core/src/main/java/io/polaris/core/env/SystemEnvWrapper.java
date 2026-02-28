package io.polaris.core.env;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * @author Qt
 * @since Apr 23, 2024
 */
class SystemEnvWrapper extends SimpleEnv {

	private final String name;

	public SystemEnvWrapper(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}


	@Override
	public void set(String key, String value) {
	}

	/**
	 * 获取系统环境变量值
	 * 
	 * 查找策略：
	 * 1. 首先按原键名查找
	 * 2. 如果未找到且键名不是全大写，则转换为大写后再次查找
	 * 
	 * @param key 环境变量键名
	 * @return 环境变量值，如果未找到则返回null
	 */
	@Override
	public String get(String key) {
		Map<String, String> env = System.getenv();
		String val = getenv(env, key);
		if (val != null) {
			return val;
		}
		String upperCase = key.toUpperCase();
		if (!key.equals(upperCase)) {
			val = getenv(env, upperCase);
			return val;
		}
		return null;
	}

	/**
	 * 从环境变量映射中获取指定键的值，支持多种键格式的查找
	 * 
	 * 查找优先级：
	 * 1. 直接匹配原始键
	 * 2. 将键中的点号(.)替换为下划线(_)后查找
	 * 3. 将键中的连字符(-)替换为下划线(_)后查找
	 * 4. 同时包含点号和连字符时，先替换点号再替换连字符后查找
	 * 
	 * @param env 环境变量映射表
	 * @param key 要查找的键名
	 * @return 找到的环境变量值，如果未找到则返回null
	 */
	@Nullable
	private String getenv(Map<String, String> env, String key) {
		String val = env.get(key);
		if (val != null) {
			return val;
		}

		boolean hasDot = key.indexOf('.') > -1;
		boolean hasHyphen = key.indexOf('-') > -1;
		if (hasDot) {
			String noDotKey = key.replace('.', '_');
			val = env.get(noDotKey);
			if (val != null) {
				return val;
			}
		}

		if (hasHyphen) {
			String noHyphenKey = key.replace('-', '_');
			val = env.get(noHyphenKey);
			if (val != null) {
				return val;
			}
			if (hasDot) {
				String noHyphenKeyNoDotKey = noHyphenKey.replace('.', '_');
				val = env.get(noHyphenKeyNoDotKey);
				return val;
			}
		}
		return null;
	}

	@Override
	public void remove(String key) {
	}

	@Override
	public Set<String> keys() {
		return System.getenv().keySet();
	}
}

package io.polaris.core.env;

import java.util.Set;

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

	@Override
	public String get(String key) {
		String val = System.getenv(key);
		if (val == null) {
			String envKey = key.replace('.', '_').toUpperCase();
			val = System.getenv(envKey);
			if (val == null && envKey.indexOf('-') > -1) {
				String envKey2 = envKey.replace('-', '_');
				val = System.getenv(envKey2);
			}
		}
		return val;
	}

	@Override
	public void remove(String key) {
	}

	@Override
	public Set<String> keys() {
		return System.getenv().keySet();
	}
}

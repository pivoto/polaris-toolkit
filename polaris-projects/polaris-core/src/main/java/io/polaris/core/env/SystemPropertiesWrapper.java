package io.polaris.core.env;

import java.util.Set;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
class SystemPropertiesWrapper implements Env {

	private final String name;

	public SystemPropertiesWrapper(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void set(String key, String value) {
		System.setProperty(key, value);
	}

	@Override
	public String get(String key) {
		return System.getProperty(key);
	}

	@Override
	public void remove(String key) {
		System.setProperty(key, null);
	}

	@Override
	public Set<String> keys() {
		return System.getProperties().stringPropertyNames();
	}
}


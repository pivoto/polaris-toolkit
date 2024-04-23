package io.polaris.core.env;

import java.util.Properties;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
class PropertiesWrapper implements Env {

	private final Properties properties;
	private final String name;

	public PropertiesWrapper(Properties properties) {
		this(null, properties);
	}

	public PropertiesWrapper(String name, Properties properties) {
		this.name = name;
		this.properties = properties;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void set(String key, String value) {
		properties.setProperty(key, value);
	}

	@Override
	public String get(String key) {
		return properties.getProperty(key);
	}

	@Override
	public void remove(String key) {
		properties.remove(key);
	}

	@Override
	public Set<String> keys() {
		return properties.stringPropertyNames();
	}
}

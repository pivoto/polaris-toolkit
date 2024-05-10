package io.polaris.core.env;

import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
class MapWrapper implements Env {

	private final String name;
	private final Map<String, String> properties;

	public MapWrapper(Map<String, String> properties) {
		this.name = null;
		this.properties = properties;
	}

	public MapWrapper(String name, Map<String, String> properties) {
		this.name = name;
		this.properties = properties;
	}

	@Override
	public String name() {
		return name;
	}
	@Override
	public void set(String key, String value) {
		properties.put(key, value);
	}

	@Override
	public String get(String key) {
		return properties.get(key);
	}

	@Override
	public void remove(String key) {
		properties.remove(key);
	}

	@Override
	public Set<String> keys() {
		return properties.keySet();
	}

}

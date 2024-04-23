package io.polaris.core.env;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public interface Env {

	default String name() {
		return null;
	}

	default void set(String key, String value) {
		// ignore
	}


	default void remove(String key) {
		// ignore
	}

	default String get(String key, String defaultValue) {
		String val = get(key);
		return (val == null) ? defaultValue : val;
	}

	String get(String key);

	Set<String> keys();

	default Properties asProperties() {
		Properties properties = new Properties();
		for (String key : keys()) {
			properties.setProperty(key, get(key));
		}
		return properties;
	}

	default Map<String, String> asMap() {
		Map<String, String> properties = new HashMap<>();
		for (String key : keys()) {
			properties.put(key, get(key));
		}
		return properties;
	}


	static Env wrap(Properties properties) {
		return new PropertiesWrapper(properties);
	}

	static Env wrap(String name, Properties properties) {
		return new PropertiesWrapper(name, properties);
	}

	static Env wrap(Map<String, String> properties) {
		return new MapWrapper(properties);
	}

	static Env wrap(String name, Map<String, String> properties) {
		return new MapWrapper(name, properties);
	}

	static DelegateEnv delegate(Env properties) {
		return new DelegateEnv(properties.name(), properties);
	}

	static DelegateEnv delegate(String name, Env properties) {
		return new DelegateEnv(name, properties);
	}
}

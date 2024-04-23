package io.polaris.core.env;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public class DelegateEnv implements Env {

	private final String name;
	private Env delegate;

	public DelegateEnv(String name, final Env delegate) {
		this.name = name;
		this.delegate = delegate;
	}

	@Override
	public String name() {
		return name;
	}

	public Env getDelegate() {
		return delegate;
	}

	public void setDelegate(final Env delegate) {
		this.delegate = delegate;
	}

	@Override
	public void set(final String key, final String value) {
		if (delegate != null) {
			delegate.set(key, value);
		}
	}


	@Override
	public void remove(final String key) {
		if (delegate != null) {
			delegate.remove(key);
		}
	}

	@Override
	public String get(final String key, final String defaultValue) {
		if (delegate != null) {
			return delegate.get(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public String get(final String key) {
		if (delegate != null) {
			return delegate.get(key);
		}
		return null;
	}

	@Override
	public Set<String> keys() {
		if (delegate != null) {
			return delegate.keys();
		}
		return Collections.emptySet();
	}

	@Override
	public Properties asProperties() {
		if (delegate != null) {
			return delegate.asProperties();
		}
		return new Properties();
	}

	@Override
	public Map<String, String> asMap() {
		if (delegate != null) {
			return delegate.asMap();
		}
		return new HashMap<>();
	}
}


package io.polaris.core.env;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Qt
 * @since Jan 07, 2025
 */
class FileEnv implements Env {

	private final String name;
	private final Env delegate;


	public FileEnv(String name, String propertyFile) {
		this.name = name;
		this.delegate = loadProperties(name, propertyFile);
	}

	private Env loadProperties(String name, String propertyFile) {
		if (propertyFile != null) {
			GroupEnv group = GroupEnv.newInstance(name);
			try {
				try (FileInputStream fis = new FileInputStream(propertyFile);) {
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
				Enumeration<URL> urls = classLoader.getResources(propertyFile);
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

	@Override
	public String name() {
		return name;
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

package io.polaris.core.msg;

import io.polaris.core.collection.Iterables;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * @author Qt
 * @since 1.8
 */
public class AggregateResourceBundles {
	public static final ResourceBundle.Control CONTROL = new AggregateControl();

	public static String aggregateNames(String... baseNames) {
		StringJoiner joiner = new StringJoiner(",");
		for (String baseName : baseNames) {
			joiner.add(baseName);
		}
		String baseName = joiner.toString();
		return baseName;
	}

	public static ResourceBundle getBundle(String baseName) {
		return ResourceBundle.getBundle(baseName, CONTROL);
	}

	public static ResourceBundle getBundle(String baseName, Locale locale) {
		return ResourceBundle.getBundle(baseName, locale, CONTROL);
	}

	public static ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {
		return ResourceBundle.getBundle(baseName, locale, loader, CONTROL);
	}

	public static ResourceBundle getBundle(String... baseNames) {
		String baseName = aggregateNames(baseNames);
		return ResourceBundle.getBundle(baseName, CONTROL);
	}


	public static ResourceBundle getBundle(Locale locale, String... baseNames) {
		return ResourceBundle.getBundle(aggregateNames(baseNames), locale, CONTROL);
	}

	public static ResourceBundle getBundle(Locale locale, ClassLoader loader, String... baseNames) {
		return ResourceBundle.getBundle(aggregateNames(baseNames), locale, loader, CONTROL);
	}

	public static class AggregateResourceBundle extends ResourceBundle {
		private final Properties properties;

		protected AggregateResourceBundle(Properties properties) {
			this.properties = properties;
		}

		@Override
		protected Object handleGetObject(String key) {
			return properties.get(key);
		}

		@Override
		public Enumeration<String> getKeys() {
			return Iterables.enumeration(properties.stringPropertyNames());
		}

		@Override
		public void setParent(ResourceBundle parent) {
			super.setParent(parent);
		}

		public Properties getProperties() {
			return properties;
		}
	}

	public static class AggregateControl extends ResourceBundle.Control {
		@Override
		public ResourceBundle newBundle(
			String baseName,
			Locale locale,
			String format,
			ClassLoader loader,
			boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
			// only *.properties files can be aggregated. Other formats are delegated to the default implementation
			if (!"java.properties".equals(format)) {
				return super.newBundle(baseName, locale, format, loader, reload);
			}

			String[] names = baseName.split("[,|]+");
			String[] resourceNames = new String[names.length];
			for (int i = 0; i < names.length; i++) {
				resourceNames[i] = toBundleName(names[i].trim(), locale).replace(".", "/") + ".properties";
			}
			Properties properties = loadProperties(loader, resourceNames);
			return new AggregateResourceBundle(properties);
		}
	}

	static Properties loadProperties(ClassLoader loader, String... resourceNames) throws IOException {
		Properties aggregatedProperties = new Properties();
		for (String resourceName : resourceNames) {
			Enumeration<URL> urls = run(() -> {
				try {
					return loader.getResources(resourceName);
				} catch (IOException e) {
					return Collections.emptyEnumeration();
				}
			});
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				Properties properties = new Properties();
				properties.load(url.openStream());
				aggregatedProperties.putAll(properties);
			}
		}
		return aggregatedProperties;
	}


	static <T> T run(PrivilegedAction<T> action) {
		return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
	}

}

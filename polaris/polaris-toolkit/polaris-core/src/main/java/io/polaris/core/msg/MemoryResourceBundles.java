package io.polaris.core.msg;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Qt
 * @since 1.8
 */
public class MemoryResourceBundles {
	public static final ResourceBundle.Control CONTROL = new MemoryControl();

	public static ResourceBundle getBundle(String baseName) {
		return ResourceBundle.getBundle(baseName, CONTROL);
	}

	public static ResourceBundle getBundle(String baseName, Locale locale) {
		return ResourceBundle.getBundle(baseName, locale, CONTROL);
	}

	public static ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {
		return ResourceBundle.getBundle(baseName, locale, loader, CONTROL);
	}

	public static class MemoryControl extends ResourceBundle.Control {
		@Override
		public ResourceBundle newBundle(
			String baseName,
			Locale locale,
			String format,
			ClassLoader loader,
			boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
			return new MemoryResourceBundle(baseName, locale);
		}
	}
}

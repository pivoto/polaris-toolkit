package io.polaris.core.env;

import java.io.InputStream;
import java.util.Properties;

import lombok.Getter;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
class InternalProperties {
	public static final String INTERNAL_PROPERTIES_PATH = "META-INF/polaris/internal.properties";
	public static final InternalProperties INSTANCE = new InternalProperties();
	@Getter
	private Properties properties = new Properties();

	private InternalProperties() {
		try (InputStream in = InternalProperties.class.getClassLoader()
			.getResourceAsStream(INTERNAL_PROPERTIES_PATH)) {
			if (in != null) {
				properties.load(in);
			}
		} catch (Exception ignore) {
		}
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
}

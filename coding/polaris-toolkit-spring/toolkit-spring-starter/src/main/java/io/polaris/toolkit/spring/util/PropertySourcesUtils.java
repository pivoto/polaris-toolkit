package io.polaris.toolkit.spring.util;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
public class PropertySourcesUtils {

	public static Map<String, Object> getSubProperties(Iterable<PropertySource<?>> propertySources, String prefix) {
		AbstractEnvironment environment = new AbstractEnvironment() {
		};
		MutablePropertySources mutablePropertySources = environment.getPropertySources();
		for (PropertySource<?> source : propertySources) {
			mutablePropertySources.addLast(source);
		}
		return getSubProperties(environment, prefix);
	}

	public static Map<String, Object> getSubProperties(ConfigurableEnvironment environment, String prefix) {
		Map<String, Object> subProperties = new LinkedHashMap<>();
		MutablePropertySources propertySources = environment.getPropertySources();
		String normalizedPrefix = prefix.endsWith(".") ? prefix : prefix + ".";
		for (PropertySource<?> source : propertySources) {
			if (source instanceof EnumerablePropertySource) {
				for (String name : ((EnumerablePropertySource<?>) source).getPropertyNames()) {
					if (!subProperties.containsKey(name) && name.startsWith(normalizedPrefix)) {
						String subName = name.substring(normalizedPrefix.length());
						if (!subProperties.containsKey(subName)) {
							Object value = source.getProperty(name);
							if (value instanceof String) {
								value = environment.resolvePlaceholders((String) value);
							}
							subProperties.put(subName, value);
						}
					}
				}
			}
		}
		return Collections.unmodifiableMap(subProperties);
	}

}

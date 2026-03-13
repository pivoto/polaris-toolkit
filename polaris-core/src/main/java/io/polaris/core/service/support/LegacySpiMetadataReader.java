package io.polaris.core.service.support;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import io.polaris.core.annotation.Alternative;
import io.polaris.core.classpath.reader.ClassMetadata;
import io.polaris.core.service.ServiceDefault;
import io.polaris.core.service.ServiceName;
import io.polaris.core.service.ServiceOrder;
import io.polaris.core.service.ServiceProperties;
import io.polaris.core.service.ServiceProperty;
import io.polaris.core.service.SpiMetadataReader;
import io.polaris.core.service.SpiProperties;

/**
 * @author Qt
 * @since Mar 13, 2026
 */
@SuppressWarnings("deprecation")
public class LegacySpiMetadataReader implements SpiMetadataReader {
	@Override
	public int priority() {
		return 100;
	}

	@Override
	public String getName(Class<?> clazz) {
		if (clazz.isAnnotationPresent(ServiceName.class)) {
			return clazz.getAnnotation(ServiceName.class).value();
		}
		return null;
	}

	@Nullable
	@Override
	public Map<String, String> getProperties(Class<?> clazz) {
		Map<String, String> properties = new HashMap<>();
		if (clazz.isAnnotationPresent(ServiceProperties.class)) {
			ServiceProperties annos = clazz.getAnnotation(ServiceProperties.class);
			for (ServiceProperty anno : annos.value()) {
				properties.put(anno.name(), anno.value());
			}
		}
		if (clazz.isAnnotationPresent(ServiceProperty.class)) {
			ServiceProperty anno = clazz.getAnnotation(ServiceProperty.class);
			properties.put(anno.name(), anno.value());
		}
		return properties;
	}

	@Override
	public boolean isOrdered(Class<?> clazz) {
		return clazz.isAnnotationPresent(ServiceOrder.class);
	}

	@Override
	public boolean isDefault(Class<?> clazz) {
		return clazz.isAnnotationPresent(ServiceDefault.class);
	}

	@Nullable
	@Override
	public Integer getOrder(Class<?> clazz) {
		if (clazz.isAnnotationPresent(ServiceOrder.class)) {
			return clazz.getAnnotation(ServiceOrder.class).value();
		}
		return null;
	}

	@Nullable
	@Override
	public Integer getDefaultOrder(Class<?> clazz) {
		if (clazz.isAnnotationPresent(ServiceDefault.class)) {
			return clazz.getAnnotation(ServiceDefault.class).value();
		}
		return null;
	}

	@Override
	public boolean isSpiClass(ClassMetadata target, Class<?> spiClass) {
		return false;
	}

}

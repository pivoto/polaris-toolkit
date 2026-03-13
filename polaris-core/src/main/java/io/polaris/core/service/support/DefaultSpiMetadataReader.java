package io.polaris.core.service.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.polaris.core.classpath.reader.AnnotationMetadata;
import io.polaris.core.classpath.reader.ClassMetadata;
import io.polaris.core.service.*;

/**
 * @author Qt
 * @since Mar 13, 2026
 */
public class DefaultSpiMetadataReader implements SpiMetadataReader {
	@Override
	public int priority() {
		return 0;
	}

	@Override
	public String getName(Class<?> clazz) {
		if (clazz.isAnnotationPresent(SpiName.class)) {
			return clazz.getAnnotation(SpiName.class).value();
		}
		return null;
	}

	@Nullable
	@Override
	public Map<String, String> getProperties(Class<?> clazz) {
		Map<String, String> properties = new HashMap<>();
		if (clazz.isAnnotationPresent(SpiProperties.class)) {
			SpiProperties annos = clazz.getAnnotation(SpiProperties.class);
			for (SpiProperty anno : annos.value()) {
				properties.put(anno.name(), anno.value());
			}
		}
		if (clazz.isAnnotationPresent(SpiProperty.class)) {
			SpiProperty anno = clazz.getAnnotation(SpiProperty.class);
			properties.put(anno.name(), anno.value());
		}
		return properties;
	}

	@Override
	public boolean isOrdered(Class<?> clazz) {
		return clazz.isAnnotationPresent(SpiOrder.class);
	}

	@Override
	public boolean isDefault(Class<?> clazz) {
		return clazz.isAnnotationPresent(SpiDefault.class);
	}

	@Nullable
	@Override
	public Integer getOrder(Class<?> clazz) {
		if (clazz.isAnnotationPresent(SpiOrder.class)) {
			return clazz.getAnnotation(SpiOrder.class).value();
		}
		return null;
	}

	@Nullable
	@Override
	public Integer getDefaultOrder(Class<?> clazz) {
		if (clazz.isAnnotationPresent(SpiDefault.class)) {
			return clazz.getAnnotation(SpiDefault.class).value();
		}
		return null;
	}


	@Override
	public boolean isSpiClass(ClassMetadata target, Class<?> spiClass) {
		if (target.isConcrete()) {
			String typeName = spiClass.getName();
			// 是否实现目标接口
			boolean hasSpiInterface = false;
			String[] interfaceNames = target.getInterfaceNames();
			for (String interfaceName : interfaceNames) {
				if (interfaceName.equals(typeName)) {
					hasSpiInterface = true;
					break;
				}
			}

			if (hasSpiInterface) {
				List<AnnotationMetadata> annotations = target.getAnnotations();
				for (AnnotationMetadata annotation : annotations) {
					if (annotation.annotationType().equals(Spi.class.getName())) {
						// 存在@Spi注解
						return true;
					}
				}
				for (String interfaceName : interfaceNames) {
					if (interfaceName.equals(SpiLoadable.class.getName()) || interfaceName.equals(StatefulSpiLoadable.class.getName())) {
						// 存在接口
						return true;
					}
				}
			}
		}
		return false;
	}

}

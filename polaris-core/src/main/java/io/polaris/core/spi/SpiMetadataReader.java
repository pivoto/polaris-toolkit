package io.polaris.core.spi;

import java.util.Map;

import javax.annotation.Nullable;

import io.polaris.core.classpath.reader.ClassMetadata;

/**
 * @author Qt
 * @since Mar 13, 2026
 */
public interface SpiMetadataReader {

	default int priority() {
		return Integer.MAX_VALUE;
	}

	@Nullable
	String getName(Class<?> clazz);

	@Nullable
	Map<String, String> getProperties(Class<?> clazz);

	boolean isOrdered(Class<?> clazz);

	boolean isDefault(Class<?> clazz);

	@Nullable
	Integer getOrder(Class<?> clazz);

	@Nullable
	Integer getDefaultOrder(Class<?> clazz);

	boolean isSpiClass(ClassMetadata target, Class<?> spiClass);

}

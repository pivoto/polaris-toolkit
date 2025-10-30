package io.polaris.core.cache;

import javax.annotation.Nullable;

import io.polaris.core.service.SpiLoaders;
import io.polaris.core.service.StatefulServiceLoader;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
public interface MemCacheFactory {

	<K, V> MemCache<K, V> build(int maxCapacity, boolean accessOrder);

	default <K, V> MemCache<K, V> build(int maxCapacity) {
		return build(maxCapacity, false);
	}

	@Nullable
	static MemCacheFactory defaultFactory() {
		StatefulServiceLoader<MemCacheFactory> loader = SpiLoaders.loadStateful(MemCacheFactory.class);
		return loader.service();
	}

	@Nullable
	static <K, V> MemCache<K, V> defaultBuild(int maxCapacity, boolean accessOrder) {
		MemCacheFactory factory = defaultFactory();
		if (factory == null) {
			return null;
		}
		return factory.build(maxCapacity, accessOrder);
	}
}

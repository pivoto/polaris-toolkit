package io.polaris.core.cache;

import javax.annotation.Nullable;

import io.polaris.core.spi.Spi;
import io.polaris.core.spi.SpiLoaders;
import io.polaris.core.spi.StatefulSpiLoadable;
import io.polaris.core.spi.StatefulSpiLoader;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
@Spi("内存缓存工厂")
public interface MemCacheFactory extends StatefulSpiLoadable {

	<K, V> MemCache<K, V> build(int maxCapacity, boolean accessOrder);

	default <K, V> MemCache<K, V> build(int maxCapacity) {
		return build(maxCapacity, false);
	}

	@Nullable
	static MemCacheFactory defaultFactory() {
		StatefulSpiLoader<MemCacheFactory> loader = SpiLoaders.loadStateful(MemCacheFactory.class);
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

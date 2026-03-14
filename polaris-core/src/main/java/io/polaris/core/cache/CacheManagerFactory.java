package io.polaris.core.cache;

import java.util.Optional;

import javax.annotation.Nullable;

import io.polaris.core.spi.SpiLoadable;
import io.polaris.core.spi.SpiLoaders;
import io.polaris.core.spi.StatefulSpiLoader;

/**
 * @author Qt
 */
public interface CacheManagerFactory extends SpiLoadable {

	CacheManager getCacheManager();


	@Nullable
	static CacheManagerFactory defaultCacheManagerFactory() {
		StatefulSpiLoader<CacheManagerFactory> loader = SpiLoaders.loadStateful(CacheManagerFactory.class);
		return loader.service();
	}

	@Nullable
	static CacheManager defaultCacheManager() {
		return Optional.ofNullable(defaultCacheManagerFactory()).map(CacheManagerFactory::getCacheManager).orElse(null);
	}

}

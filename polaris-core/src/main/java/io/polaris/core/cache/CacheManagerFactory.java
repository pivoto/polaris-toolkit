package io.polaris.core.cache;

import java.util.Optional;

import javax.annotation.Nullable;

import io.polaris.core.spi.Spi;
import io.polaris.core.spi.SpiLoadable;
import io.polaris.core.spi.SpiLoaders;
import io.polaris.core.spi.StatefulSpiLoadable;
import io.polaris.core.spi.StatefulSpiLoader;

/**
 * @author Qt
 */
@Spi("缓存管理器工厂")
public interface CacheManagerFactory extends StatefulSpiLoadable {

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

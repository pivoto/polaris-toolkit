package io.polaris.core.cache;

import io.polaris.core.service.ServiceLoadable;
import io.polaris.core.service.SpiLoaders;
import io.polaris.core.service.StatefulServiceLoader;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author Qt
 * @since 1.8
 */
public interface CacheManagerFactory extends ServiceLoadable {

	CacheManager getCacheManager();


	@Nullable
	static CacheManagerFactory defaultCacheManagerFactory() {
		StatefulServiceLoader<CacheManagerFactory> loader = SpiLoaders.loadStateful(CacheManagerFactory.class);
		return loader.service();
	}

	@Nullable
	static CacheManager defaultCacheManager() {
		return Optional.ofNullable(defaultCacheManagerFactory()).map(CacheManagerFactory::getCacheManager).orElse(null);
	}

}

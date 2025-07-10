package io.polaris.core.cache;

import io.polaris.core.service.ServiceLoadable;
import io.polaris.core.service.StatefulServiceLoader;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author Qt
 * @since 1.8
 */
public interface ICacheManagerFactory extends ServiceLoadable {

	ICacheManager getCacheManager();


	@Nullable
	static ICacheManagerFactory defaultCacheManagerFactory() {
		StatefulServiceLoader<ICacheManagerFactory> loader = StatefulServiceLoader.load(ICacheManagerFactory.class);
		return loader.service();
	}

	@Nullable
	static ICacheManager defaultCacheManager() {
		return Optional.ofNullable(defaultCacheManagerFactory()).map(ICacheManagerFactory::getCacheManager).orElse(null);
	}

}

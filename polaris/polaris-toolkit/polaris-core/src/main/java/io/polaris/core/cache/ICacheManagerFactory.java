package io.polaris.core.cache;

import io.polaris.core.service.StatefulServiceLoader;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author Qt
 * @since 1.8
 */
public interface ICacheManagerFactory {

	ICacheManager get();


	@Nullable
	static ICacheManagerFactory defaultCacheManagerFactory() {
		StatefulServiceLoader<ICacheManagerFactory> loader = StatefulServiceLoader.load(ICacheManagerFactory.class);
		return loader.service();
	}

	@Nullable
	static ICacheManager defaultCacheManager() {
		return Optional.ofNullable(defaultCacheManagerFactory()).map(ICacheManagerFactory::get).orElse(null);
	}

}

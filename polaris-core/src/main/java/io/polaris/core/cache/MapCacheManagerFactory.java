package io.polaris.core.cache;

import io.polaris.core.service.ServiceDefault;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceDefault(Integer.MAX_VALUE)
public class MapCacheManagerFactory implements ICacheManagerFactory {

	private MapCacheManager cacheManager;

	public MapCacheManagerFactory() {
		cacheManager = new MapCacheManager();
	}

	@Override
	public CacheManager getCacheManager() {
		return cacheManager;
	}
}

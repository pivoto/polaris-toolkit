package io.polaris.core.cache;

import io.polaris.core.service.SpiDefault;

/**
 * @author Qt
 */
@SpiDefault(Integer.MAX_VALUE)
public class MapCacheManagerFactory implements CacheManagerFactory {

	private final MapCacheManager cacheManager;

	public MapCacheManagerFactory() {
		cacheManager = new MapCacheManager();
	}

	@Override
	public CacheManager getCacheManager() {
		return cacheManager;
	}
}

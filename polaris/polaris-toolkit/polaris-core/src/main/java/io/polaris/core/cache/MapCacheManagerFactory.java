package io.polaris.core.cache;

import io.polaris.core.service.ServiceDefault;
import io.polaris.core.service.ServiceOrder;

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
	public ICacheManager get() {
		return cacheManager;
	}
}

package io.polaris.core.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractCacheManager implements CacheManager {
	private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap(16);

	@Override
	@SuppressWarnings("unchecked")
	public <K, V> Cache<K, V> getCache(String name) {
		Cache<K, V> cache = cacheMap.get(name);
		if (cache != null) {
			return cache;
		}
		synchronized (cacheMap) {
			cache = cacheMap.get(name);
			if (cache == null) {
				cache = createCache(name);
				cacheMap.put(name, cache);
			}
			return cache;
		}
	}

	protected <K, V> Cache<K, V> createCache(String name) {
		return null;
	}
}

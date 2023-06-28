package io.polaris.core.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractCacheManager implements ICacheManager {
	private final ConcurrentMap<String, ICache> cacheMap = new ConcurrentHashMap(16);

	@Override
	@SuppressWarnings("unchecked")
	public <K, V> ICache<K, V> get(String name) {
		ICache<K, V> iCache = cacheMap.get(name);
		if (iCache != null) {
			return iCache;
		}
		synchronized (cacheMap) {
			iCache = cacheMap.get(name);
			if (iCache == null) {
				iCache = createCache(name);
				cacheMap.put(name, iCache);
			}
			return iCache;
		}
	}

	protected <K, V> ICache<K, V> createCache(String name) {
		return null;
	}
}

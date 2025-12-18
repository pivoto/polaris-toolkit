package io.polaris.core.cache;

import javax.annotation.Nullable;

/**
 * @author Qt
 */
public interface CacheManager {

	@Nullable
	<K, V> Cache<K, V> getCache(String name);

}

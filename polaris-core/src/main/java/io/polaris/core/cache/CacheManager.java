package io.polaris.core.cache;

import javax.annotation.Nullable;

/**
 * @author Qt
 * @since 1.8
 */
public interface CacheManager {

	@Nullable
	<K, V> Cache<K, V> getCache(String name);

}

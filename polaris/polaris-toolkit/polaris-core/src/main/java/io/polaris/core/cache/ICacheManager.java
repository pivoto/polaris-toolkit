package io.polaris.core.cache;

import javax.annotation.Nullable;

/**
 * @author Qt
 * @since 1.8
 */
public interface ICacheManager {

	@Nullable
	<K, V> ICache<K, V> get(String name);

}

package io.polaris.core.cache;

import io.polaris.core.service.ServiceDefault;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
@ServiceDefault(Integer.MAX_VALUE)
public class DefaultMemCacheFactory implements MemCacheFactory {
	@Override
	public <K, V> MemCache<K, V> build(int maxCapacity, boolean accessOrder) {
		return new MapCache<>(maxCapacity, accessOrder);
	}
}

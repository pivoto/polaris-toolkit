package io.polaris.core.cache;

import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
public class MapCacheManager extends AbstractCacheManager {
	public static final int DEFAULT_MAX_CAPACITY = 10240;
	public static final String KEY_MAX_CAPACITY = MapCacheManager.class.getName() + ".maxCapacity";
	public static final String KEY_ACCESS_ORDER = MapCacheManager.class.getName() + ".accessOrder";
	private int maxCapacity = DEFAULT_MAX_CAPACITY;
	private boolean accessOrder;

	public MapCacheManager() {
		init();
	}

	private void init() {
		{
			String val = GlobalStdEnv.get(KEY_MAX_CAPACITY);
			if (Strings.isNotBlank(val)) {
				try {
					maxCapacity = Integer.parseInt(val);
				} catch (NumberFormatException e) {
				}
			}
		}
		{
			String val = GlobalStdEnv.get(KEY_ACCESS_ORDER);
			if (Strings.isNotBlank(val)) {
				accessOrder = Boolean.parseBoolean(val);
			}
		}
	}

	@Override
	protected <K, V> ICache<K, V> createCache(String name) {
		if (maxCapacity > 0) {
			return new MapCache<>(maxCapacity, accessOrder);
		} else {
			return new MapCache<>();
		}
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public boolean isAccessOrder() {
		return accessOrder;
	}

	public void setAccessOrder(boolean accessOrder) {
		this.accessOrder = accessOrder;
	}
}

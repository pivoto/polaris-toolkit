package io.polaris.core.map;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class LimitedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	private int maxCapacity = 0;

	public LimitedLinkedHashMap(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public LimitedLinkedHashMap(int maxCapacity, boolean accessOrder) {
		super(16, .75f, accessOrder);
		this.maxCapacity = maxCapacity;
	}

	public LimitedLinkedHashMap(int maxCapacity, boolean accessOrder, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor, accessOrder);
		this.maxCapacity = maxCapacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxCapacity;
	}
}

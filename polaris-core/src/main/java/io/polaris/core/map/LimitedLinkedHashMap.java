package io.polaris.core.map;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class LimitedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;
	private int maxCapacity = 0;


	public LimitedLinkedHashMap(int maxCapacity) {
		this(maxCapacity, false);
	}

	public LimitedLinkedHashMap(int maxCapacity, boolean accessOrder) {
		this(maxCapacity, accessOrder, 128, .75f);
	}

	public LimitedLinkedHashMap(int maxCapacity, boolean accessOrder, int initialCapacity) {
		this(maxCapacity, accessOrder, initialCapacity, .75f);
	}

	public LimitedLinkedHashMap(int maxCapacity, boolean accessOrder, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor, accessOrder);
		this.maxCapacity = maxCapacity;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxCapacity;
	}
}

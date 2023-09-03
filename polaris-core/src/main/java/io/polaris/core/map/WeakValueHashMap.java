package io.polaris.core.map;

import io.polaris.core.map.reference.WeakValueReference;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class WeakValueHashMap<K, V> extends ValueReferenceMap<K, V> {

	public WeakValueHashMap(int initialCapacity, float loadFactor) {
		super(new HashMap<>(initialCapacity, loadFactor), WeakValueReference::new);
	}

	public WeakValueHashMap(int initialCapacity) {
		super(new HashMap<>(initialCapacity), WeakValueReference::new);
	}

	public WeakValueHashMap() {
		super(new HashMap<>(), WeakValueReference::new);
	}

	public WeakValueHashMap(Map<K, V> t) {
		super(new HashMap<>(), WeakValueReference::new);
		putAll(t);
	}

}

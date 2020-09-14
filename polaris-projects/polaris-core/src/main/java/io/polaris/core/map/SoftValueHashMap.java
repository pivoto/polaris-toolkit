package io.polaris.core.map;

import io.polaris.core.map.reference.SoftValueReference;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class SoftValueHashMap<K, V> extends ValueReferenceMap<K, V> {

	public SoftValueHashMap(int initialCapacity, float loadFactor) {
		super(new HashMap<>(initialCapacity, loadFactor), SoftValueReference::new);
	}

	public SoftValueHashMap(int initialCapacity) {
		super(new HashMap<>(initialCapacity), SoftValueReference::new);
	}

	public SoftValueHashMap() {
		super(new HashMap<>(), SoftValueReference::new);
	}

	public SoftValueHashMap(Map<K, V> t) {
		super(new HashMap<>(), SoftValueReference::new);
		putAll(t);
	}


}

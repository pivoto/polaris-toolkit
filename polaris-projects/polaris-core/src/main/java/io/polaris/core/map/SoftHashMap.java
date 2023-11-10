package io.polaris.core.map;

import io.polaris.core.map.reference.ReferenceType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class SoftHashMap<K, V> extends ReferenceMap<K, V> {

	public SoftHashMap(int initialCapacity, float loadFactor) {
		super(new HashMap<>(initialCapacity, loadFactor), ReferenceType.SOFT);
	}

	public SoftHashMap(int initialCapacity) {
		super(new HashMap<>(initialCapacity), ReferenceType.SOFT);
	}

	public SoftHashMap() {
		super(new HashMap<>(), ReferenceType.SOFT);
	}

	public SoftHashMap(Map<K, V> t) {
		super(new HashMap<>(), ReferenceType.SOFT);
		putAll(t);
	}

}

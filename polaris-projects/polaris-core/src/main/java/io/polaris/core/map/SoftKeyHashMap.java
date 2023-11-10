package io.polaris.core.map;

import io.polaris.core.map.reference.SoftKeyReference;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class SoftKeyHashMap<K, V> extends KeyReferenceMap<K, V> {

	public SoftKeyHashMap(int initialCapacity, float loadFactor) {
		super(new HashMap<>(initialCapacity, loadFactor), SoftKeyReference::new);
	}

	public SoftKeyHashMap(int initialCapacity) {
		super(new HashMap<>(initialCapacity), SoftKeyReference::new);
	}

	public SoftKeyHashMap() {
		super(new HashMap<>(), SoftKeyReference::new);
	}

	public SoftKeyHashMap(Map<K, V> t) {
		super(new HashMap<>(), SoftKeyReference::new);
		putAll(t);
	}


}

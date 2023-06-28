package io.polaris.core.map;

import io.polaris.core.map.reference.WeakKeyReference;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class WeakKeyHashMap<K, V> extends KeyReferenceMap<K, V> {

	public WeakKeyHashMap(int initialCapacity, float loadFactor) {
		super(new HashMap<>(initialCapacity, loadFactor), WeakKeyReference::new);
	}

	public WeakKeyHashMap(int initialCapacity) {
		super(new HashMap<>(initialCapacity), WeakKeyReference::new);
	}

	public WeakKeyHashMap() {
		super(new HashMap<>(), WeakKeyReference::new);
	}

	public WeakKeyHashMap(Map<K, V> t) {
		super(new HashMap<>(), WeakKeyReference::new);
		putAll(t);
	}

}

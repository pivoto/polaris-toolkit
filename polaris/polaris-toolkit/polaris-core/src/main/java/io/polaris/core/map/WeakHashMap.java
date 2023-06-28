package io.polaris.core.map;

import io.polaris.core.map.reference.ReferenceType;
import io.polaris.core.map.reference.WeakValueReference;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class WeakHashMap<K, V> extends ReferenceMap<K, V> {

	public WeakHashMap(int initialCapacity, float loadFactor) {
		super(new HashMap<>(initialCapacity, loadFactor), ReferenceType.WEAK);
	}

	public WeakHashMap(int initialCapacity) {
		super(new HashMap<>(initialCapacity), ReferenceType.WEAK);
	}

	public WeakHashMap() {
		super(new HashMap<>(), ReferenceType.WEAK);
	}

	public WeakHashMap(Map<K, V> t) {
		super(new HashMap<>(), ReferenceType.WEAK);
		putAll(t);
	}

}

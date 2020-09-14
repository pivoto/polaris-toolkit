package io.polaris.core.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class SetMultiMap<K, V> extends BaseMultiMap<K, V, Set<V>> {

	public SetMultiMap(Map<K, Set<V>> raw, Supplier<Set<V>> supplier) {
		super(raw, supplier);
	}

	public SetMultiMap(Map<K, Set<V>> raw) {
		super(raw, HashSet::new);
	}

	public SetMultiMap(Supplier<Set<V>> supplier) {
		super(new HashMap<>(), supplier);
	}

	public SetMultiMap() {
		super(new HashMap<>(), HashSet::new);
	}

	@Override
	public V getOne(Object key) {
		Set<V> c = get(key);
		if (c == null || c.isEmpty()) {
			return null;
		}
		return c.iterator().next();
	}

	@Override
	public Set<V> putOne(K key, V value) {
		Set<V> c = get(key);
		if (c == null) {
			c = supplier.get();
			put(key, c);
		}
		c.add(value);
		return c;
	}


}

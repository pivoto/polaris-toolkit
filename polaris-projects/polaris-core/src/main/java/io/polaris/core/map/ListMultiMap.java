package io.polaris.core.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class ListMultiMap<K, V> extends BaseMultiMap<K, V, List<V>> {

	public ListMultiMap(Map<K, List<V>> raw, Supplier<List<V>> supplier) {
		super(raw, supplier);
	}

	public ListMultiMap(Map<K, List<V>> raw) {
		super(raw, ArrayList::new);
	}

	public ListMultiMap(Supplier<List<V>> supplier) {
		super(new HashMap<>(), supplier);
	}

	public ListMultiMap() {
		super(new HashMap<>(), ArrayList::new);
	}

	@Override
	public V getOne(Object key) {
		List<V> c = get(key);
		if (c == null || c.isEmpty()) {
			return null;
		}
		return c.get(0);
	}

	@Override
	public List<V> putOne(K key, V value) {
		List<V> c = get(key);
		if (c == null) {
			c = supplier.get();
			put(key, c);
		}
		c.add(value);
		return c;
	}


}

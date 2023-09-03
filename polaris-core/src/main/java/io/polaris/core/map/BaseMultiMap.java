package io.polaris.core.map;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class BaseMultiMap<K, V, E extends Collection<V>> implements MultiMap<K, V, E> {
	protected final Map<K, E> raw;
	protected final Supplier<E> supplier;

	public BaseMultiMap(Map<K, E> raw, Supplier<E> supplier) {
		this.raw = raw;
		this.supplier = supplier;
	}

	@Override
	public String toString() {
		return raw.toString();
	}

	@Override
	public E putAll(K key, V[] values) {
		return putAll(key, Arrays.asList(values));
	}

	@Override
	public E putAll(K key, Iterable<V> values) {
		E vs = null;
		for (V value : values) {
			vs = putOne(key, value);
		}
		return vs;
	}

	@Override
	public boolean removeOne(K key, V value) {
		E vs = get(key);
		if (vs != null) {
			return vs.remove(value);
		}
		return false;
	}

	@Override
	public boolean containsOneValue(Object value) {
		Iterator<Entry<K, E>> i = entrySet().iterator();
		if (value == null) {
			while (i.hasNext()) {
				Entry<K, E> e = i.next();
				if (e.getValue() == null) {
					return true;
				}
			}
		} else {
			while (i.hasNext()) {
				Entry<K, E> e = i.next();
				if (e.getValue().contains(value)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int size() {
		return raw.size();
	}

	@Override
	public boolean isEmpty() {
		return raw.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return raw.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return raw.containsValue(value);
	}

	@Override
	public E get(Object key) {
		return raw.get(key);
	}

	@Override
	public E put(K key, E value) {
		return raw.put(key, value);
	}

	@Override
	public E remove(Object key) {
		return raw.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends E> m) {
		raw.putAll(m);
	}

	@Override
	public void clear() {
		raw.clear();
	}

	@Override
	public Set<K> keySet() {
		return raw.keySet();
	}

	@Override
	public Collection<E> values() {
		return raw.values();
	}

	@Override
	public Set<Entry<K, E>> entrySet() {
		return raw.entrySet();
	}
}

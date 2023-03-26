package io.polaris.core.map;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class WeakHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {

	private Map<K, ValueRef<K, V>> hash;
	private ReferenceQueue<ValueRef<K, V>> queue = new ReferenceQueue();

	public WeakHashMap(int initialCapacity, float loadFactor) {
		hash = new HashMap(initialCapacity, loadFactor);
	}

	public WeakHashMap(int initialCapacity) {
		hash = new HashMap(initialCapacity);
	}

	public WeakHashMap() {
		hash = new HashMap();
	}

	public WeakHashMap(Map t) {
		this(Math.max(2 * t.size(), 11), 0.75f);
		putAll(t);
	}

	private void processQueue() {
		ValueRef ref;
		while ((ref = (ValueRef) queue.poll()) != null) {
			if (ref == hash.get(ref.key)) {
				hash.remove(ref.key);
			}
		}
	}

	public int size() {
		processQueue();
		return hash.size();
	}

	public boolean isEmpty() {
		processQueue();
		return hash.isEmpty();
	}

	public boolean containsKey(Object key) {
		processQueue();
		return hash.containsKey(key);
	}

	public V get(Object key) {
		processQueue();
		ValueRef<K, V> ref = hash.get(key);
		if (ref != null)
			return ref.get();
		return null;
	}

	public V put(K key, V value) {
		processQueue();
		ValueRef<K, V> ref = hash.put(key, new ValueRef(key, value, queue));
		if (ref != null)
			return ref.get();
		return null;
	}

	public V remove(Object key) {
		processQueue();
		ValueRef<K, V> removed = hash.remove(key);
		if (removed == null) {
			return null;
		}
		return removed.get();
	}

	public void clear() {
		processQueue();
		hash.clear();
	}

	@Override
	public java.util.Set<Entry<K, V>> entrySet() {
		if (hash.isEmpty()) {
			return Collections.<K, V>emptyMap().entrySet();
		}
		Map<K, V> map = new HashMap<K, V>();
		for (Entry<K, ValueRef<K, V>> entry : hash.entrySet()) {
			V currentValueForEntry = entry.getValue().get();
			if (currentValueForEntry != null) {
				map.put(entry.getKey(), currentValueForEntry);
			}
		}
		return map.entrySet();
	}

	static class ValueRef<K, V> extends WeakReference<V> {
		private final K key;

		ValueRef(K k, V v, ReferenceQueue<V> q) {
			super(v, q);
			this.key = k;
		}
	}
}

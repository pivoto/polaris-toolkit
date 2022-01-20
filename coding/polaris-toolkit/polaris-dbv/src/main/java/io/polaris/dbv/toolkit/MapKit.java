package io.polaris.dbv.toolkit;

import java.util.*;

/**
 * @author Qt
 * @version Jun 04, 2019
 */
public class MapKit {


	public static <K, V> CaseInsensitiveMap<K, V> newCaseInsensitiveMap(Map<K, V> map) {
		return new CaseInsensitiveMap<>(map);
	}

	public static <K, V> CaseInsensitiveMap<K, V> newCaseInsensitiveLinkedHashMap() {
		return new CaseInsensitiveMap<>(new LinkedHashMap<K, V>());
	}

	public static <K, V> CaseInsensitiveMap<K, V> newCaseInsensitiveLinkedHashMap(int initialCapacity, float loadFactor) {
		return new CaseInsensitiveMap<>(new LinkedHashMap<K, V>(initialCapacity, loadFactor));
	}


	public static class CaseInsensitiveMap<K, V> implements Map<K, V> {

		private Map<K, V> map;

		public CaseInsensitiveMap() {
			this.map = new LinkedHashMap<>();
		}

		public CaseInsensitiveMap(Map<K, V> map) {
			if (map instanceof CaseInsensitiveMap) {
				this.map = ((CaseInsensitiveMap<K, V>) map).map;
			} else {
				int size = map.size();
				if (size == 0) {
					this.map = map;
				} else {
					Set<Entry<K, V>> set = map.entrySet();
					List<Entry<K, V>> list = new ArrayList<>(size);
					for (Iterator<Entry<K, V>> iter = set.iterator(); iter.hasNext(); ) {
						Entry<K, V> entry = iter.next();
						list.add(entry);
						iter.remove();
					}
					for (Entry<K, V> entry : list) {
						this.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}

		@SuppressWarnings("unchecked")
		static <T> T toUpperCase(T key) {
			if (key != null && key.getClass() == String.class) {
				key = (T) ((String) key).toUpperCase();
			}
			return key;
		}

		@Override
		public int size() {
			return map.size();
		}

		@Override
		public boolean isEmpty() {
			return map.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return map.containsKey(toUpperCase(key));
		}

		@Override
		public boolean containsValue(Object value) {
			return map.containsValue(value);
		}

		@Override
		public V get(Object key) {
			return map.get(toUpperCase(key));
		}

		@Override
		public V put(K key, V value) {
			return map.put(toUpperCase(key), value);
		}

		@Override
		public V remove(Object key) {
			return map.remove(toUpperCase(key));
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> m) {
			if (m.getClass() == this.getClass()) {
				map.putAll(m);
			} else {
				for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
					K key = entry.getKey();
					map.put(toUpperCase(key), entry.getValue());
				}
			}
		}

		@Override
		public void clear() {
			map.clear();
		}

		@Override
		public Set<K> keySet() {
			return map.keySet();
		}

		@Override
		public Collection<V> values() {
			return map.values();
		}

		@Override
		public Set<Entry<K, V>> entrySet() {
			return map.entrySet();
		}

		@Override
		public String toString() {
			return map.toString();
		}
	}

}

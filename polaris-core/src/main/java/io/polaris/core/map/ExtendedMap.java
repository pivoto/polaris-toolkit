package io.polaris.core.map;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import io.polaris.core.lang.Objs;

/**
 * @author Qt
 */
public class ExtendedMap<K, V> implements Map<K, V> {
	protected final Map<K, V> raw;
	protected final Map<K, V> extension;

	public ExtendedMap(@Nonnull Map<K, V> raw) {
		this(raw, new HashMap<>());
	}

	public ExtendedMap(@Nonnull Map<K, V> raw, Map<K, V> extension) {
		this.raw = raw;
		this.extension = extension != null ? extension : new HashMap<>();
	}

	public Map<K, V> toMergedMap() {
		Map<K, V> merged = new HashMap<>();
		merged.putAll(raw);
		merged.putAll(extension);
		return merged;
	}

	@Override
	public String toString() {
		return toMergedMap().toString();
	}

	@Override
	public int size() {
		return toMergedMap().size();
	}

	@Override
	public boolean isEmpty() {
		return extension.isEmpty() && raw.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return extension.containsKey(key) || raw.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return extension.containsValue(value) || raw.containsValue(value);
	}

	@Override
	public V get(Object key) {
		V v = extension.get(key);
		if (v != null) {
			return v;
		}
		return raw.get(key);
	}

	@Override
	public V put(K key, V value) {
		return extension.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return extension.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		m.forEach(this::put);
	}

	@Override
	public void clear() {
		extension.clear();
	}

	transient Set<K> keySet;
	transient Collection<V> values;

	@Override
	public Set<K> keySet() {
		Set<K> ks = keySet;
		if (ks == null) {
			ks = new AbstractSet<K>() {
				@Override
				public Iterator<K> iterator() {
					return new Iterator<K>() {
						private Iterator<Entry<K, V>> i = entrySet().iterator();

						@Override
						public boolean hasNext() {
							return i.hasNext();
						}

						@Override
						public K next() {
							return i.next().getKey();
						}

						@Override
						public void remove() {
							i.remove();
						}
					};
				}

				@Override
				public int size() {
					return ExtendedMap.this.size();
				}

				@Override
				public boolean isEmpty() {
					return ExtendedMap.this.isEmpty();
				}

				@Override
				public void clear() {
					ExtendedMap.this.clear();
				}

				@Override
				public boolean contains(Object k) {
					return ExtendedMap.this.containsKey(k);
				}
			};
			keySet = ks;
		}
		return ks;
	}

	@Override
	public Collection<V> values() {
		Collection<V> vals = values;
		if (vals == null) {
			vals = new AbstractCollection<V>() {
				@Override
				public Iterator<V> iterator() {
					return new Iterator<V>() {
						private Iterator<Entry<K, V>> i = entrySet().iterator();

						@Override
						public boolean hasNext() {
							return i.hasNext();
						}

						@Override
						public V next() {
							return i.next().getValue();
						}

						@Override
						public void remove() {
							i.remove();
						}
					};
				}

				@Override
				public int size() {
					return ExtendedMap.this.size();
				}

				@Override
				public boolean isEmpty() {
					return ExtendedMap.this.isEmpty();
				}

				@Override
				public void clear() {
					ExtendedMap.this.clear();
				}

				@Override
				public boolean contains(Object v) {
					return ExtendedMap.this.containsValue(v);
				}
			};
			values = vals;
		}
		return vals;
	}

	transient Set<Map.Entry<K, V>> entrySet;

	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<Map.Entry<K, V>> es;
		return (es = entrySet) == null ? (entrySet = new InnerEntrySet()) : es;
	}


	final class InnerEntrySet extends AbstractSet<Entry<K, V>> {

		@Override
		public int size() {
			return ExtendedMap.this.size();
		}

		@Override
		public void clear() {
			ExtendedMap.this.clear();
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new Iterator<Entry<K, V>>() {
				private final Set<Entry<K, V>> entrySet = ExtendedMap.this.extension.entrySet();
				private final Iterator<Entry<K, V>> it = entrySet.iterator();
				private final Set<Entry<K, V>> entrySetRaw = ExtendedMap.this.raw.entrySet();
				private final Iterator<Entry<K, V>> itRaw = entrySetRaw.iterator();
				private boolean writable;

				@Override
				public boolean hasNext() {
					return it.hasNext() || itRaw.hasNext();
				}

				@Override
				public Entry<K, V> next() {
					Entry<K, V> next;
					if (it.hasNext()) {
						next = it.next();
						writable = true;
					} else {
						next = itRaw.next();
						writable = false;
					}
					return new Map.Entry<K, V>() {
						@Override
						public K getKey() {
							return next.getKey();
						}

						@Override
						public V getValue() {
							return next.getValue();
						}

						@Override
						public V setValue(V value) {
							if (writable) {
								V old = next.getValue();
								next.setValue(value);
								return old;
							} else {
								return null;
							}
						}
					};
				}

				@Override
				public void remove() {
					if (writable) {
						it.remove();
					}
				}
			};
		}

		@Override
		public boolean add(Entry<K, V> e) {
			ExtendedMap.this.put(e.getKey(), e.getValue());
			return true;
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			Map.Entry<K, V> e = (Map.Entry<K, V>) o;
			K key = e.getKey();
			if (!ExtendedMap.this.containsKey(key)) {
				return false;
			}
			Object val = ExtendedMap.this.get(key);
			return Objs.equals(val, e.getValue());
		}

		@Override
		public final boolean remove(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}

			@SuppressWarnings("unchecked")
			Map.Entry<K, V> e = (Map.Entry<K, V>) o;
			K key = e.getKey();
			V removed = ExtendedMap.this.remove(key);
			return removed != null;
		}
	}
}

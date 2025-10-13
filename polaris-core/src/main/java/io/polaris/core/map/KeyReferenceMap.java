package io.polaris.core.map;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import io.polaris.core.lang.Objs;
import io.polaris.core.map.reference.ReferenceType;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class KeyReferenceMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {

	private final BiFunction<K, ReferenceQueue<K>, Reference<K>> referenceFactory;
	private final Map<Reference<K>, V> raw;
	private final ReferenceQueue<K> queue = new ReferenceQueue<>();

	public KeyReferenceMap(Map<Reference<K>, V> raw, BiFunction<K, ReferenceQueue<K>, Reference<K>> referenceFactory) {
		this.raw = raw;
		this.referenceFactory = referenceFactory;
	}

	public KeyReferenceMap(Map<Reference<K>, V> raw, ReferenceType referenceType) {
		this.raw = raw;
		this.referenceFactory = referenceType::buildKeyReference;
	}

	public KeyReferenceMap(Supplier<Map<Reference<K>, V>> supplier, ReferenceType referenceType) {
		this(supplier.get(), referenceType);
	}


	private Reference<K> buildKeyReference(K key) {
		return referenceFactory.apply(key, queue);
	}

	private void processQueue() {
		Reference<? extends K> ref;
		while ((ref = queue.poll()) != null) {
			raw.remove(ref);
		}
	}

	@Override
	public int size() {
		processQueue();
		return raw.size();
	}

	@Override
	public boolean isEmpty() {
		processQueue();
		return raw.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		processQueue();
		return raw.containsKey(buildKeyReference((K) key));
	}

	@Override
	public V get(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		processQueue();
		return raw.get(buildKeyReference((K) key));
	}

	@Override
	public V put(K key, V value) {
		if (key == null) {
			throw new NullPointerException();
		}
		if (value == null) {
			throw new NullPointerException();
		}
		processQueue();
		return raw.put(buildKeyReference((K) key), value);
	}

	@Override
	public V remove(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		processQueue();
		return raw.remove(buildKeyReference((K) key));
	}

	@Override
	public void clear() {
		processQueue();
		raw.clear();
	}

	@Nonnull
	@Override
	public java.util.Set<Entry<K, V>> entrySet() {
		if (raw.isEmpty()) {
			return Collections.<K, V>emptyMap().entrySet();
		}
		processQueue();
		if (raw.isEmpty()) {
			return Collections.<K, V>emptyMap().entrySet();
		}
		Map<K, V> map = new HashMap<K, V>();
		for (Entry<Reference<K>, V> entry : raw.entrySet()) {
			V value = entry.getValue();
			if (value != null) {
				K key = entry.getKey().get();
				if (key != null) {
					map.put(key, value);
				}
			}
		}
		return map.entrySet();
	}

	// region 代理接口默认方法

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		processQueue();
		raw.replaceAll((k, v) -> function.apply(k.get(), v));
	}

	@Override
	public V putIfAbsent(K key, V value) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		return raw.putIfAbsent(keyRef, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		processQueue();
		Reference<K> keyRef = buildKeyReference((K) key);
		return raw.remove(keyRef, value);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		return raw.replace(keyRef, oldValue, newValue);
	}

	@Override
	public V replace(K key, V value) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		return raw.replace(keyRef, value);
	}

	@Override
	public V computeIfAbsent(K key, @Nonnull Function<? super K, ? extends V> mappingFunction) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		return raw.computeIfAbsent(keyRef, (k) -> mappingFunction.apply(k.get()));
	}

	@Override
	public V computeIfPresent(K key, @Nonnull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		return raw.computeIfPresent(keyRef, (k, v) -> remappingFunction.apply(k.get(), v));
	}

	@Override
	public V compute(K key, @Nonnull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		return raw.compute(keyRef, (k, v) -> remappingFunction.apply(k.get(), v));
	}

	@Override
	public V merge(K key, @Nonnull V value, @Nonnull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		return raw.merge(keyRef, value, remappingFunction);
	}

	// endregion


	final class InnerEntrySet extends AbstractSet<Entry<K, V>> {
		@Override
		public int size() {
			return KeyReferenceMap.this.size();
		}

		@Override
		public void clear() {
			KeyReferenceMap.this.clear();
		}

		@Nonnull
		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new Iterator<Entry<K, V>>() {
				private final Set<Entry<Reference<K>, V>> entrySet = KeyReferenceMap.this.raw.entrySet();
				private final Iterator<Entry<Reference<K>, V>> it = entrySet.iterator();

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public Entry<K, V> next() {
					Entry<Reference<K>, V> next = it.next();

					return new Entry<K, V>() {

						@Override
						public K getKey() {
							return next.getKey().get();
						}

						@Override
						public V getValue() {
							return next.getValue();
						}

						@Override
						public V setValue(V value) {
							V old = next.getValue();
							next.setValue(value);
							return old;
						}
					};
				}

				@Override
				public void remove() {
					it.remove();
				}
			};
		}

		@Override
		public boolean add(Entry<K, V> e) {
			KeyReferenceMap.this.put(e.getKey(), e.getValue());
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
			if (!KeyReferenceMap.this.containsKey(key)) {
				return false;
			}
			Object val = KeyReferenceMap.this.get(key);
			return Objs.equals(val, e.getValue());
		}

		@Override
		public boolean remove(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}

			@SuppressWarnings("unchecked")
			Map.Entry<K, V> e = (Map.Entry<K, V>) o;
			K key = e.getKey();
			V removed = KeyReferenceMap.this.remove(key);
			return removed != null;
		}
	}
}

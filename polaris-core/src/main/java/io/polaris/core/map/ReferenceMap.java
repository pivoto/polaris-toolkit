package io.polaris.core.map;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import io.polaris.core.function.FunctionWithArgs3;
import io.polaris.core.lang.Objs;
import io.polaris.core.map.reference.ReferenceType;
import io.polaris.core.map.reference.ValueReference;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ReferenceMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {

	private final BiFunction<K, ReferenceQueue<K>, Reference<K>> keyRreferenceFactory;
	private final FunctionWithArgs3<Reference<K>, V, ReferenceQueue<V>, ValueReference<Reference<K>, V>> valueReferenceFactory;
	private final Map<Reference<K>, ValueReference<Reference<K>, V>> raw;
	private final ReferenceQueue<K> keyQueue = new ReferenceQueue<>();
	private final ReferenceQueue<V> valueQueue = new ReferenceQueue<>();


	public ReferenceMap(Map<Reference<K>, ValueReference<Reference<K>, V>> raw, ReferenceType referenceType) {
		this.raw = raw;
		this.valueReferenceFactory = referenceType::buildValueReference;
		this.keyRreferenceFactory = referenceType::buildKeyReference;
	}

	public ReferenceMap(Supplier<Map<Reference<K>, ValueReference<Reference<K>, V>>> supplier, ReferenceType referenceType) {
		this(supplier.get(), referenceType);
	}

	private Reference<K> buildKeyReference(K key) {
		return keyRreferenceFactory.apply(key, keyQueue);
	}

	private ValueReference<Reference<K>, V> buildValueReference(Reference<K> key, V value) {
		return valueReferenceFactory.apply(key, value, valueQueue);
	}

	private void processQueue() {
		{
			// remove by key
			Reference<? extends K> ref;
			while ((ref = keyQueue.poll()) != null) {
				raw.remove(ref);
			}
		}
		{
			// remove by value
			Reference<? extends V> ref;
			while ((ref = valueQueue.poll()) != null) {
				if (ref instanceof ValueReference) {
					ValueReference<Reference<K>, V> vr = (ValueReference<Reference<K>, V>) ref;
					Reference<K> keyRef = vr.key();
					raw.remove(keyRef, vr);
				}
			}
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
		ValueReference<Reference<K>, V> ref = raw.get(buildKeyReference((K) key));
		if (ref != null) {
			return ref.value();
		}
		return null;
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
		Reference<K> keyRef = buildKeyReference((K) key);
		ValueReference<Reference<K>, V> ref = raw.put(keyRef, buildValueReference(keyRef, value));
		if (ref != null) {
			return ref.value();
		}
		return null;
	}

	@Override
	public V remove(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		processQueue();
		ValueReference<Reference<K>, V> removed = raw.remove(buildKeyReference((K) key));
		if (removed == null) {
			return null;
		}
		return removed.value();
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
		return new InnerEntrySet();
	}


	// region 代理接口默认方法

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		processQueue();
		raw.replaceAll((k, v) ->
			buildValueReference(k, function.apply(k.get(), v.value()))
		);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		ValueReference<Reference<K>, V> ref = raw.putIfAbsent(keyRef, buildValueReference(keyRef, value));
		return ref == null ? null : ref.value();
	}

	@Override
	public boolean remove(Object key, Object value) {
		processQueue();
		Reference<K> keyRef = buildKeyReference((K) key);
		return raw.remove(keyRef, buildValueReference(keyRef, (V) value));
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		return raw.replace(keyRef, buildValueReference(keyRef, oldValue), buildValueReference(keyRef, newValue));
	}

	@Override
	public V replace(K key, V value) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		ValueReference<Reference<K>, V> ref = raw.replace(keyRef, buildValueReference(keyRef, value));
		return ref == null ? null : ref.value();
	}

	@Override
	public V computeIfAbsent(K key, @Nonnull Function<? super K, ? extends V> mappingFunction) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		ValueReference<Reference<K>, V> ref = raw.computeIfAbsent(keyRef, (k) -> buildValueReference(k, mappingFunction.apply(k.get())));
		return ref == null ? null : ref.value();
	}

	@Override
	public V computeIfPresent(K key, @Nonnull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		ValueReference<Reference<K>, V> ref = raw.computeIfPresent(keyRef, (k, v) ->
			buildValueReference(k, v == null ? null : remappingFunction.apply(k.get(), v.value())));
		return ref == null ? null : ref.value();
	}

	@Override
	public V compute(K key, @Nonnull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		ValueReference<Reference<K>, V> ref = raw.compute(keyRef, (k, v) ->
			buildValueReference(k, remappingFunction.apply(k.get(), v == null ? null : v.value())));
		return ref == null ? null : ref.value();
	}

	@Override
	public V merge(K key, @Nonnull V value, @Nonnull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		processQueue();
		Reference<K> keyRef = buildKeyReference(key);
		ValueReference<Reference<K>, V> ref = raw.merge(keyRef, buildValueReference(keyRef, value), (v1, v2) ->
			buildValueReference(keyRef, remappingFunction.apply(v1.value(), v2.value())));
		return ref == null ? null : ref.value();
	}

	// endregion


	final class InnerEntrySet extends AbstractSet<Entry<K, V>> {
		@Override
		public int size() {
			return ReferenceMap.this.size();
		}

		@Override
		public void clear() {
			ReferenceMap.this.clear();
		}

		@Nonnull
		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new Iterator<Entry<K, V>>() {
				private final Set<Entry<Reference<K>, ValueReference<Reference<K>, V>>> entrySet = ReferenceMap.this.raw.entrySet();
				private final Iterator<Entry<Reference<K>, ValueReference<Reference<K>, V>>> it = entrySet.iterator();

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public Entry<K, V> next() {
					Entry<Reference<K>, ValueReference<Reference<K>, V>> next = it.next();

					return new Entry<K, V>() {

						@Override
						public K getKey() {
							return next.getKey().get();
						}

						@Override
						public V getValue() {
							return next.getValue().value();
						}

						@Override
						public V setValue(V value) {
							V old = next.getValue().value();
							next.setValue(buildValueReference(next.getKey(), value));
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
			ReferenceMap.this.put(e.getKey(), e.getValue());
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
			if (!ReferenceMap.this.containsKey(key)) {
				return false;
			}
			Object val = ReferenceMap.this.get(key);
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
			V removed = ReferenceMap.this.remove(key);
			return removed != null;
		}
	}
}

package io.polaris.core.map;

import io.polaris.core.function.FunctionWithArgs3;
import io.polaris.core.lang.Objs;
import io.polaris.core.map.reference.ReferenceType;
import io.polaris.core.map.reference.ValueReference;

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

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings({"unchecked"})
public class ValueReferenceMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {

	private final FunctionWithArgs3<K, V, ReferenceQueue<V>, ValueReference<K, V>> referenceFactory;
	private final Map<K, ValueReference<K, V>> raw;
	private final ReferenceQueue<V> queue = new ReferenceQueue<>();

	public ValueReferenceMap(Map<K, ValueReference<K, V>> raw, FunctionWithArgs3<K, V, ReferenceQueue<V>, ValueReference<K, V>> referenceFactory) {
		this.raw = raw;
		this.referenceFactory = referenceFactory;
	}

	public ValueReferenceMap(Map<K, ValueReference<K, V>> raw, ReferenceType referenceType) {
		this.raw = raw;
		this.referenceFactory = referenceType::buildValueReference;
	}

	public ValueReferenceMap(Supplier<Map<K, ValueReference<K, V>>> supplier, ReferenceType referenceType) {
		this(supplier.get(), referenceType);
	}


	private ValueReference<K, V> buildValueReference(K key, V value) {
		return referenceFactory.apply(key, value, queue);
	}

	private void processQueue() {
		Reference<? extends V> ref;
		while ((ref = queue.poll()) != null) {
			if (ref instanceof ValueReference) {
				ValueReference<K, V> vr = (ValueReference<K, V>) ref;
				raw.remove(vr.key(),vr);
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
		return raw.containsKey(key);
	}

	@Override
	public V get(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		processQueue();
		ValueReference<K, V> ref = raw.get(key);
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
		ValueReference<K, V> ref = raw.put(key, buildValueReference(key, value));
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
		ValueReference<K, V> removed = raw.remove(key);
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
	public java.util.Set<Map.Entry<K, V>> entrySet() {
		if (raw.isEmpty()) {
			return Collections.<K, V>emptyMap().entrySet();
		}
		processQueue();
		if (raw.isEmpty()) {
			return Collections.<K, V>emptyMap().entrySet();
		}
		Map<K, V> map = new HashMap<>();
		for (Map.Entry<K, ValueReference<K, V>> entry : raw.entrySet()) {
			V value = entry.getValue().value();
			if (value != null) {
				map.put(entry.getKey(), value);
			}
		}
		return map.entrySet();
	}

	// region 代理接口默认方法

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		processQueue();
		raw.replaceAll((k, v) -> buildValueReference(k, function.apply(k, v.value())));
	}

	@Override
	public V putIfAbsent(K key, V value) {
		processQueue();
		ValueReference<K, V> ref = raw.putIfAbsent(key, buildValueReference(key, value));
		return ref == null ? null : ref.value();
	}

	@Override
	public boolean remove(Object key, Object value) {
		processQueue();
		return raw.remove(key, buildValueReference((K) key, (V) value));
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		processQueue();
		return raw.replace(key, buildValueReference(key, oldValue), buildValueReference(key, newValue));
	}

	@Override
	public V replace(K key, V value) {
		processQueue();
		ValueReference<K, V> ref = raw.replace(key, buildValueReference(key, value));
		return ref == null ? null : ref.value();
	}

	@Override
	public V computeIfAbsent(K key, @Nonnull Function<? super K, ? extends V> mappingFunction) {
		processQueue();
		ValueReference<K, V> ref = raw.computeIfAbsent(key, (k) -> buildValueReference(k, mappingFunction.apply(k)));
		return ref == null ? null : ref.value();
	}

	@Override
	public V computeIfPresent(K key, @Nonnull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		processQueue();
		ValueReference<K, V> ref = raw.computeIfPresent(key, (k, v) ->
			buildValueReference(k, remappingFunction.apply(k, v.value())));
		return ref == null ? null : ref.value();
	}

	@Override
	public V compute(K key, @Nonnull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		processQueue();
		ValueReference<K, V> ref = raw.compute(key, (k, v) ->
			buildValueReference(k, remappingFunction.apply(k, v == null ? null : v.value())));
		return ref == null ? null : ref.value();
	}

	@Override
	public V merge(K key, @Nonnull V value, @Nonnull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		processQueue();
		ValueReference<K, V> ref = raw.merge(key, buildValueReference(key, value), (v1, v2) ->
			buildValueReference(key, remappingFunction.apply(v1.value(), v2.value())));
		return ref == null ? null : ref.value();
	}

	// endregion


	final class InnerEntrySet extends AbstractSet<Entry<K, V>> {
		@Override
		public int size() {
			return ValueReferenceMap.this.size();
		}

		@Override
		public void clear() {
			ValueReferenceMap.this.clear();
		}

		@Nonnull
		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new Iterator<Entry<K, V>>() {
				private final Set<Entry<K, ValueReference<K, V>>> entrySet = ValueReferenceMap.this.raw.entrySet();
				private final Iterator<Entry<K, ValueReference<K, V>>> it = entrySet.iterator();

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public Entry<K, V> next() {
					Entry<K, ValueReference<K, V>> next = it.next();

					return new Entry<K, V>() {

						@Override
						public K getKey() {
							return next.getKey();
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
			ValueReferenceMap.this.put(e.getKey(), e.getValue());
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
			if (!ValueReferenceMap.this.containsKey(key)) {
				return false;
			}
			Object val = ValueReferenceMap.this.get(key);
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
			V removed = ValueReferenceMap.this.remove(key);
			return removed != null;
		}
	}
}

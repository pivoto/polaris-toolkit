package io.polaris.core.cache;

import io.polaris.core.map.Maps;
import io.polaris.core.tuple.Ref;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class MapCache<K, V> implements ICache<K, V> {

	private final Map<K, Ref<V>> map;

	public MapCache() {
		this.map = new ConcurrentHashMap<>();
	}

	public MapCache(int maxCapacity) {
		if (maxCapacity > 0) {
			this.map = Collections.synchronizedMap(Maps.newLimitCapacityMap(maxCapacity));
		} else {
			this.map = new ConcurrentHashMap<>();
		}
	}

	public MapCache(int maxCapacity, boolean accessOrder) {
		if (maxCapacity > 0) {
			this.map = Collections.synchronizedMap(Maps.newLimitCapacityMap(maxCapacity, accessOrder));
		} else {
			this.map = new ConcurrentHashMap<>();
		}
	}

	public MapCache(Map<K, Ref<V>> map) {
		this.map = map;
	}

	@Nonnull
	@Override
	public Ref<V> get(@Nonnull K key) {
		return map.get(key);
	}

	@Nullable
	@Override
	public V getIfPresent(@Nonnull K key) {
		Ref<V> ref = map.get(key);
		return ref == null ? null : ref.get();
	}

	@Nullable
	@Override
	public V get(K key, Supplier<V> loader) {
		Ref<V> ref = map.get(key);
		return ref == null ? loader.get() : ref.get(loader);
	}

	@Override
	public Ref<V> putIfAbsent(@Nonnull K key, @Nonnull V value) {
		return map.putIfAbsent(key, Ref.of(value));
	}

	@Override
	public void put(@Nonnull K key, @Nonnull V value) {
		map.put(key, Ref.of(value));
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		m.forEach((k, v) -> map.put(k, Ref.of(v)));
	}

	@Override
	public void remove(K key) {
		map.remove(key);
	}

	@Override
	public void clear() {
		map.clear();
	}
}

package io.polaris.core.map;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
public class DelegateMap<K, V> implements Map<K, V> {
	protected final Map<K, V> raw;

	public DelegateMap(Map<K, V> raw) {
		this.raw = raw;
	}

	@Override
	public String toString() {
		return raw.toString();
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
	public V get(Object key) {
		return raw.get(key);
	}

	@Override
	public V put(K key, V value) {
		return raw.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return raw.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		m.forEach(this::put);
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
	public Collection<V> values() {
		return raw.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return raw.entrySet();
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return raw.getOrDefault(key, defaultValue);
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		raw.forEach(action);
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		raw.replaceAll(function);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return raw.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return raw.remove(key, value);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return raw.replace(key, oldValue, newValue);
	}

	@Override
	public V replace(K key, V value) {
		return raw.replace(key, value);
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return raw.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return raw.computeIfPresent(key, remappingFunction);
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return raw.compute(key, remappingFunction);
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return raw.merge(key, value, remappingFunction);
	}
}

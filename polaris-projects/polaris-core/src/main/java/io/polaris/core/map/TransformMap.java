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
public class TransformMap<K, V> implements Map<K, V> {
	private final Map<K, V> raw;
	private final Function<Object, K> keyTransformer;
	private final Function<Object, V> valueTransformer;

	public TransformMap(Map<K, V> raw) {
		this.raw = raw;
		this.keyTransformer = null;
		this.valueTransformer = null;
	}

	public TransformMap(Map<K, V> raw, Function<Object, K> keyTransformer) {
		this.raw = raw;
		this.keyTransformer = keyTransformer;
		this.valueTransformer = null;
	}

	public TransformMap(Map<K, V> raw, Function<Object, K> keyTransformer, Function<Object, V> valueTransformer) {
		this.raw = raw;
		this.keyTransformer = keyTransformer;
		this.valueTransformer = valueTransformer;
	}

	protected K transformKey(Object key) {
		return keyTransformer==null? (K) key :keyTransformer.apply(key);
	}


	protected V transformValue(Object value) {
		return valueTransformer==null? (V) value :valueTransformer.apply(value);
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
		return raw.containsKey(transformKey(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return raw.containsValue(transformValue(value));
	}

	@Override
	public V get(Object key) {
		return raw.get(transformKey(key));
	}

	@Override
	public V put(K key, V value) {
		return raw.put(transformKey(key), transformValue(value));
	}

	@Override
	public V remove(Object key) {
		return raw.remove(transformKey(key));
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
		return raw.getOrDefault(transformKey(key), transformValue(defaultValue));
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
		return raw.putIfAbsent(transformKey(key), transformValue(value));
	}

	@Override
	public boolean remove(Object key, Object value) {
		return raw.remove(transformKey(key), transformValue(value));
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return raw.replace(transformKey(key), transformValue(oldValue), transformValue(newValue));
	}

	@Override
	public V replace(K key, V value) {
		return raw.replace(transformKey(key), transformValue(value));
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return raw.computeIfAbsent(transformKey(key), mappingFunction);
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return raw.computeIfPresent(transformKey(key), remappingFunction);
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return raw.compute(transformKey(key), remappingFunction);
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return raw.merge(transformKey(key), transformValue(value), remappingFunction);
	}
}

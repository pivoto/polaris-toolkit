package io.polaris.core.map;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Qt
 * @since 1.8
 */
public class FluentMap<K, V> {

	private final Map<K, V> map;

	public FluentMap(Map<K, V> map) {
		this.map = map;
	}

	public static <K, V> FluentMap<K, V> of(Map<K, V> map) {
		return new FluentMap<>(map);
	}

	public Map<K, V> get() {
		return map;
	}

	public FluentMap<K, V> visit(Consumer<Map<K, V>> consumer) {
		consumer.accept(map);
		return this;
	}


	public FluentMap<K, V> put(K key, V value) {
		map.put(key, value);
		return this;
	}

	public FluentMap<K, V> remove(Object key) {
		map.remove(key);
		return this;
	}

	public FluentMap<K, V> putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
		return this;
	}

	public FluentMap<K, V> clear() {
		map.clear();
		return this;
	}
}

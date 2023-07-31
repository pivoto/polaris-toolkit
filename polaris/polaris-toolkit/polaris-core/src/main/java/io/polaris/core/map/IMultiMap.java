package io.polaris.core.map;

import java.util.Collection;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public interface IMultiMap<K, V, E extends Collection<V>> extends Map<K, E> {


	V getOne(Object key);

	E putOne(K key, V value);

	E putAll(K key, V[] values);

	E putAll(K key, Iterable<V> values);

	boolean removeOne(K key, V value);

	boolean containsOneValue(Object value);
}

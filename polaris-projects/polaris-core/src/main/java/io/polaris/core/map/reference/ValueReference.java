package io.polaris.core.map.reference;

/**
 * @author Qt
 * @since 1.8
 */
public interface ValueReference<K,V> {

	K key();

	V value();
}

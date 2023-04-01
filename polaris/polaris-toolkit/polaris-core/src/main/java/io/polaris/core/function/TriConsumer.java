package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
public interface TriConsumer<T, K, V> {

	void accept(T t, K k, V v);
}

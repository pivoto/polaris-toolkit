package io.polaris.core.cache;

import io.polaris.core.tuple.Ref;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public interface ICache<K, V> {

	@Nonnull
	Ref<V> get(@Nonnull K key);

	@Nullable
	V getIfPresent(@Nonnull K key);

	@Nullable
	V get(K key, Supplier<V> loader);

	Ref<V> putIfAbsent(@Nonnull K key, @Nonnull V value);

	void put(@Nonnull K key, @Nonnull V value);

	void putAll(Map<? extends K, ? extends V> m);

	void remove(K key);

	void clear();


}

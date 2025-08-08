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
public interface Cache<K, V> {

	@Nullable
	Ref<V> get(@Nonnull K key);

	@Nullable
	default V getIfPresent(@Nonnull K key) {
		Ref<V> ref = get(key);
		return ref == null ? null : ref.get();
	}

	@Nullable
	V get(@Nonnull K key, Supplier<V> loader);

	@Nullable
	default Ref<V> putIfAbsent(@Nonnull K key, @Nullable V value) {
		Ref<V> existingValue = get(key);
		if (existingValue == null) {
			put(key, value);
		}
		return existingValue;
	}

	void put(@Nonnull K key, @Nullable V value);

	default void putAll(Map<? extends K, ? extends V> m) {
		m.forEach((k, v) -> put(k, v));
	}

	void remove(K key);

	void clear();


}

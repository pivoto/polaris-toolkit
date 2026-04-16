package io.polaris.core.tuple;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Qt
 */
@FunctionalInterface
public interface Ref<V> extends Tuple {

	V get();

	default V getRequired() throws NullPointerException {
		return Objects.requireNonNull(get());
	}

	default V get(Supplier<V> loader) {
		V v = get();
		if (v == null && loader != null) {
			v = loader.get();
		}
		return v;
	}

	static <E> Ref<E> of(final E value) {
		return new ValueRef<>(value);
	}
}

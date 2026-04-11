package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableFunction<T, R> {

	R apply(T t) throws Throwable;

	default <V> ThrowableFunction<V, R> compose(ThrowableFunction<? super V, ? extends T> before) {
		Objects.requireNonNull(before);
		return (V v) -> apply(before.apply(v));
	}

	default <V> ThrowableFunction<T, V> andThen(ThrowableFunction<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (T t) -> after.apply(apply(t));
	}

	static <T> ThrowableFunction<T, T> identity() {
		return t -> t;
	}
}

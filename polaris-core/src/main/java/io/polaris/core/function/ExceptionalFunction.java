package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalFunction<T, R> {

	R apply(T t) throws Exception;

	default <V> ExceptionalFunction<V, R> compose(ExceptionalFunction<? super V, ? extends T> before) {
		Objects.requireNonNull(before);
		return (V v) -> apply(before.apply(v));
	}

	default <V> ExceptionalFunction<T, V> andThen(ExceptionalFunction<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (T t) -> after.apply(apply(t));
	}

	static <T> ExceptionalFunction<T, T> identity() {
		return t -> t;
	}
}

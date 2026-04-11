package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalBiFunction<T, U, R> {

	R apply(T t, U u) throws Exception;

	default <V> ExceptionalBiFunction<T, U, V> andThen(ExceptionalFunction<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (T t, U u) -> after.apply(apply(t, u));
	}
}

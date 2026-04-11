package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableBiFunction<T, U, R> {

	R apply(T t, U u) throws Throwable;

	default <V> ThrowableBiFunction<T, U, V> andThen(ThrowableFunction<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (T t, U u) -> after.apply(apply(t, u));
	}
}

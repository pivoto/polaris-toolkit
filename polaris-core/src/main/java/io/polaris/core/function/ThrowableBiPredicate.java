package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableBiPredicate<T, U> {

	boolean test(T t, U u) throws Throwable;

	default ThrowableBiPredicate<T, U> and(ThrowableBiPredicate<? super T, ? super U> other) {
		Objects.requireNonNull(other);
		return (T t, U u) -> test(t, u) && other.test(t, u);
	}

	default ThrowableBiPredicate<T, U> negate() {
		return (T t, U u) -> !test(t, u);
	}

	default ThrowableBiPredicate<T, U> or(ThrowableBiPredicate<? super T, ? super U> other) {
		Objects.requireNonNull(other);
		return (T t, U u) -> test(t, u) || other.test(t, u);
	}
}

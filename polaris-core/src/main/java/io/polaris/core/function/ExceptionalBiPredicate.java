package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalBiPredicate<T, U> {

	boolean test(T t, U u) throws Exception;

	default ExceptionalBiPredicate<T, U> and(ExceptionalBiPredicate<? super T, ? super U> other) {
		Objects.requireNonNull(other);
		return (T t, U u) -> test(t, u) && other.test(t, u);
	}

	default ExceptionalBiPredicate<T, U> negate() {
		return (T t, U u) -> !test(t, u);
	}

	default ExceptionalBiPredicate<T, U> or(ExceptionalBiPredicate<? super T, ? super U> other) {
		Objects.requireNonNull(other);
		return (T t, U u) -> test(t, u) || other.test(t, u);
	}
}

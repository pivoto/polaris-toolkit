package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowablePredicate<T> {

	boolean test(T t) throws Throwable;

	default ThrowablePredicate<T> and(ThrowablePredicate<? super T> other) {
		Objects.requireNonNull(other);
		return (t) -> test(t) && other.test(t);
	}

	default ThrowablePredicate<T> negate() {
		return (t) -> !test(t);
	}

	default ThrowablePredicate<T> or(ThrowablePredicate<? super T> other) {
		Objects.requireNonNull(other);
		return (t) -> test(t) || other.test(t);
	}

	static <T> ThrowablePredicate<T> isEqual(Object targetRef) {
		return (null == targetRef)
			? Objects::isNull
			: targetRef::equals;
	}
}

package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalPredicate<T> {

	boolean test(T t) throws Exception;

	default ExceptionalPredicate<T> and(ExceptionalPredicate<? super T> other) {
		Objects.requireNonNull(other);
		return (t) -> test(t) && other.test(t);
	}

	default ExceptionalPredicate<T> negate() {
		return (t) -> !test(t);
	}

	default ExceptionalPredicate<T> or(ExceptionalPredicate<? super T> other) {
		Objects.requireNonNull(other);
		return (t) -> test(t) || other.test(t);
	}

	static <T> ExceptionalPredicate<T> isEqual(Object targetRef) {
		return (null == targetRef)
			? Objects::isNull
			: targetRef::equals;
	}
}

package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableBiConsumer<T, U> {

	void accept(T t, U u) throws Throwable;

	default ThrowableBiConsumer<T, U> andThen(ThrowableBiConsumer<? super T, ? super U> after) {
		Objects.requireNonNull(after);

		return (l, r) -> {
			accept(l, r);
			after.accept(l, r);
		};
	}
}

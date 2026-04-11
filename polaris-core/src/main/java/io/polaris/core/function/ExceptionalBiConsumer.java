package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalBiConsumer<T, U> {

	void accept(T t, U u) throws Exception;

	default ExceptionalBiConsumer<T, U> andThen(ExceptionalBiConsumer<? super T, ? super U> after) {
		Objects.requireNonNull(after);

		return (l, r) -> {
			accept(l, r);
			after.accept(l, r);
		};
	}
}

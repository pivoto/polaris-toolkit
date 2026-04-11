package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalConsumer<T> {

	void accept(T t) throws Exception;

	default ExceptionalConsumer<T> andThen(ExceptionalConsumer<? super T> after) {
		Objects.requireNonNull(after);
		return (T t) -> {
			accept(t);
			after.accept(t);
		};
	}
}

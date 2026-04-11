package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableConsumer<T> {

	void accept(T t) throws Throwable;

	default ThrowableConsumer<T> andThen(ThrowableConsumer<? super T> after) {
		Objects.requireNonNull(after);
		return (T t) -> {
			accept(t);
			after.accept(t);
		};
	}
}

package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ConsumerWithArgs<T> {

	void accept(T... args);

	default ConsumerWithArgs<T> andThen(ConsumerWithArgs<? super T> after) {
		return (T... args) -> {
			accept(args);
			after.accept(args);
		};
	}
}

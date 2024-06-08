package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ConsumerWithArgs3<A, B, C> {

	void accept(A a, B b, C c);

	default ConsumerWithArgs3<A, B, C> andThen(ConsumerWithArgs3<? super A, ? super B, ? super C> after) {
		Objects.requireNonNull(after);
		return (a, b, c) -> {
			accept(a, b, c);
			after.accept(a, b, c);
		};
	}
}

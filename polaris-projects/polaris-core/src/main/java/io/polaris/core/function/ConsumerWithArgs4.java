package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ConsumerWithArgs4<A, B, C, D> {

	void accept(A a, B b, C c, D d);

	default ConsumerWithArgs4<A, B, C, D> andThen(ConsumerWithArgs4<? super A, ? super B, ? super C, ? super D> after) {
		Objects.requireNonNull(after);
		return (a, b, c, d) -> {
			accept(a, b, c, d);
			after.accept(a, b, c, d);
		};
	}
}

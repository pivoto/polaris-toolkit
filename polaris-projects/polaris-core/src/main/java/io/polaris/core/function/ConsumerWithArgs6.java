package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ConsumerWithArgs6<A, B, C, D, E, F> {

	void accept(A a, B b, C c, D d, E e, F f);

	default ConsumerWithArgs6<A, B, C, D, E, F> andThen(ConsumerWithArgs6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> after) {
		return (a, b, c, d, e, f) -> {
			accept(a, b, c, d, e, f);
			after.accept(a, b, c, d, e, f);
		};
	}
}

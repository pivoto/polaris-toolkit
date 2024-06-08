package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ConsumerWithArgs5<A, B, C, D, E> {

	void accept(A a, B b, C c, D d, E e);

	default ConsumerWithArgs5<A, B, C, D, E> andThen(ConsumerWithArgs5<? super A, ? super B, ? super C, ? super D, ? super E> after) {
		return (a, b, c, d, e) -> {
			accept(a, b, c, d, e);
			after.accept(a, b, c, d, e);
		};
	}
}

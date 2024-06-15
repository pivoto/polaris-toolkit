package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ConsumerWithArgs7<A, B, C, D, E, F, G> {

	void accept(A a, B b, C c, D d, E e, F f, G g);

	default ConsumerWithArgs7<A, B, C, D, E, F, G> andThen(ConsumerWithArgs7<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G> after) {
		return (a, b, c, d, e, f, g) -> {
			accept(a, b, c, d, e, f, g);
			after.accept(a, b, c, d, e, f, g);
		};
	}
}

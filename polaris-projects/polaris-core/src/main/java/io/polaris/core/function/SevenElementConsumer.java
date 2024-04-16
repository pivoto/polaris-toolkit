package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SevenElementConsumer<A, B, C, D, E, F, G> {

	void accept(A a, B b, C c, D d, E e, F f, G g);
}

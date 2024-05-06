package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ConsumerWithArgs5<A, B, C, D, E> {

	void accept(A a, B b, C c, D d, E e);
}

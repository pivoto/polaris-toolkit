package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableConsumerWithArgs5<A, B, C, D, E> {

	void accept(A a, B b, C c, D d, E e) throws Throwable;

}

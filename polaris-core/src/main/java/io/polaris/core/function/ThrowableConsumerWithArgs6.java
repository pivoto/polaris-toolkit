package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableConsumerWithArgs6<A, B, C, D, E, F> {

	void accept(A a, B b, C c, D d, E e, F f) throws Throwable;

}

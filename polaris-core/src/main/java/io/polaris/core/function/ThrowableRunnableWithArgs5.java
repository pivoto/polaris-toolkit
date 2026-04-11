package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableRunnableWithArgs5<A, B, C, D, E> {

	void run(A a, B b, C c, D d, E e) throws Throwable;

}

package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableExecutableWithArgs5<A, B, C, D, E> {

	void execute(A a, B b, C c, D d, E e) throws Throwable;

}

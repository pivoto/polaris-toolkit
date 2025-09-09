package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ThrowableExecutionWithArgs5<A, B, C, D, E> {

	void execute(A a, B b, C c, D d, E e) throws Throwable;

}

package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalRunnableWithArgs5<A, B, C, D, E> {

	void run(A a, B b, C c, D d, E e) throws Exception;

}

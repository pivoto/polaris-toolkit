package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExecutableWithArgs5<A, B, C, D, E> {

	void execute(A a, B b, C c, D d, E e) throws Exception;

}

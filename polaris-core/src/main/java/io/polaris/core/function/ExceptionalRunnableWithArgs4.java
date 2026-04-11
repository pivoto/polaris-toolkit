package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalRunnableWithArgs4<A, B, C, D> {

	void run(A a, B b, C c, D d) throws Exception;

}

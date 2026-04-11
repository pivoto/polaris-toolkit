package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalRunnableWithArgs2<A, B> {

	void run(A a, B b) throws Exception;

}

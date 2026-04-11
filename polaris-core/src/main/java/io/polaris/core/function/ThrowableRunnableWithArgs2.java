package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableRunnableWithArgs2<A, B> {

	void run(A a, B b) throws Throwable;

}

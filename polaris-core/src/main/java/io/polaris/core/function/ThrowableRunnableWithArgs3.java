package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableRunnableWithArgs3<A, B, C> {

	void run(A a, B b, C c) throws Throwable;

}

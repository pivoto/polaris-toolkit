package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableExecutionWithArgs3<A, B, C> {

	void execute(A a, B b, C c) throws Throwable;

}

package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableExecutionWithArgs2<A, B> {

	void execute(A a, B b) throws Throwable;

}

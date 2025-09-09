package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ThrowableExecutionWithArgs2<A, B> {

	void execute(A a, B b) throws Throwable;

}

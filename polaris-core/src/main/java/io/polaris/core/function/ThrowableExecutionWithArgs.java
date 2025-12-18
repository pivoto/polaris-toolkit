package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableExecutionWithArgs<T> {

	void execute(T... args) throws Throwable;

}

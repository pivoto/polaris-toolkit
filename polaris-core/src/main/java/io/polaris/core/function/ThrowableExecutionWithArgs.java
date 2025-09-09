package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ThrowableExecutionWithArgs<T> {

	void execute(T... args) throws Throwable;

}

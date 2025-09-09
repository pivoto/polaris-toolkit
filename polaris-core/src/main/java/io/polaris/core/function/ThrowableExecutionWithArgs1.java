package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ThrowableExecutionWithArgs1<T> {

	void execute(T t) throws Throwable;

}

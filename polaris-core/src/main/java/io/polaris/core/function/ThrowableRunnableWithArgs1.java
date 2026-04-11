package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableRunnableWithArgs1<T> {

	void run(T t) throws Throwable;

}

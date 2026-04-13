package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableExecutableWithArgs1<T> {

	void execute(T t) throws Throwable;

}

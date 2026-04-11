package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalRunnableWithArgs1<T> {

	void run(T t) throws Exception;

}

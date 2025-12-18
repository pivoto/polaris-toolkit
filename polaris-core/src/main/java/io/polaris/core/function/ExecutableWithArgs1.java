package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExecutableWithArgs1<T> {

	void execute(T t) throws Exception;

}

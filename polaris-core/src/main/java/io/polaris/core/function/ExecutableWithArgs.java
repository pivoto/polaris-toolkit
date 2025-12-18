package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExecutableWithArgs<T> {

	void execute(T... args) throws Exception;

}

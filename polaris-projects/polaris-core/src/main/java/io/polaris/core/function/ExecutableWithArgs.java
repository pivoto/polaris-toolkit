package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ExecutableWithArgs<T> {

	void execute(T... args) throws Exception;

}

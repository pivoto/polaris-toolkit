package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface CallableWithArgs<V, T> {

	V call(T... args) throws Exception;

}

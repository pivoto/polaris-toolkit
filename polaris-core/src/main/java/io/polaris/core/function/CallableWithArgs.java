package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface CallableWithArgs<T, V> {

	V call(T... args) throws Exception;

}

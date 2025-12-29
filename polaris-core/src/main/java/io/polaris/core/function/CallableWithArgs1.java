package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface CallableWithArgs1<T, V> {

	V call(T t) throws Exception;

}

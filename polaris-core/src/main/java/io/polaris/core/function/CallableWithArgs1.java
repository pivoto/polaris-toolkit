package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface CallableWithArgs1<V, T> {

	V call(T t) throws Exception;

}

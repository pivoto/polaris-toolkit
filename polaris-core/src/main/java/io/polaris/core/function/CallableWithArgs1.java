package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface CallableWithArgs1<V, T> {

	V call(T t) throws Exception;

}

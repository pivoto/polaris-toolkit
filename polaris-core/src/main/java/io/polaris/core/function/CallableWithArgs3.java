package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface CallableWithArgs3<V, A, B, C> {

	V call(A a, B b, C c) throws Exception;

}

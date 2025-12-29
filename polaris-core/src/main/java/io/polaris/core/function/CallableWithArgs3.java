package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface CallableWithArgs3<A, B, C, V> {

	V call(A a, B b, C c) throws Exception;

}

package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface CallableWithArgs4<A, B, C, D, V> {

	V call(A a, B b, C c, D d) throws Exception;

}

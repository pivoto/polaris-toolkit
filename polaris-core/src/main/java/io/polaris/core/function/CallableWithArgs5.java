package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface CallableWithArgs5<A, B, C, D, E, V> {

	V call(A a, B b, C c, D d, E e) throws Exception;

}

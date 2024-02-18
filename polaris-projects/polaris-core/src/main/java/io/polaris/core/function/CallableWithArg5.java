package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface CallableWithArg5<V, A, B, C, D, E> {

	V call(A a, B b, C c, D d, E e) throws Exception;

}

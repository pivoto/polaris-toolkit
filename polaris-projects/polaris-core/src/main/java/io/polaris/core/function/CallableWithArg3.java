package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface CallableWithArg3<V, A, B, C> {

	V call(A a, B b, C c) throws Exception;

}

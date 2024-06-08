package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface CallableWithArgs4<V, A, B, C, D> {

	V call(A a, B b, C c, D d) throws Exception;

}

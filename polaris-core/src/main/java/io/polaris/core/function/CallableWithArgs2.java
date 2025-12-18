package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface CallableWithArgs2<V, A, B> {

	V call(A a, B b) throws Exception;

}

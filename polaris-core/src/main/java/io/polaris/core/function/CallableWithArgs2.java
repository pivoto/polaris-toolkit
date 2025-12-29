package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface CallableWithArgs2<A, B, V> {

	V call(A a, B b) throws Exception;

}

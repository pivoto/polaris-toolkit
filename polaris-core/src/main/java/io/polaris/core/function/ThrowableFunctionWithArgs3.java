package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableFunctionWithArgs3<A, B, C, R> {

	R apply(A a, B b, C c) throws Throwable;

}

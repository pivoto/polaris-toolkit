package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableFunctionWithArgs4<A, B, C, D, R> {

	R apply(A a, B b, C c, D d) throws Throwable;

}

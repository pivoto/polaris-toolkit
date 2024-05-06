package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface FunctionWithArgs4<A, B, C, D, R> {

	R apply(A a, B b, C c, D d);
}

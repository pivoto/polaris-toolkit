package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface FunctionWithArgs3<A, B, C, R> {

	R apply(A a, B b, C c);
}

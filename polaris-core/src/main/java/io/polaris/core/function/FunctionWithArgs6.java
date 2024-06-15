package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface FunctionWithArgs6<A, B, C, D, E, F, R> {

	R apply(A a, B b, C c, D d, E e, F f);
}

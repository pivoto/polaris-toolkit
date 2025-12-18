package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface FunctionWithArgs5<A, B, C, D, E, R> {

	R apply(A a, B b, C c, D d, E e);
}

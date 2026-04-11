package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalFunctionWithArgs5<A, B, C, D, E, R> {

	R apply(A a, B b, C c, D d, E e) throws Exception;

}

package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalFunctionWithArgs7<A, B, C, D, E, F, G, R> {

	R apply(A a, B b, C c, D d, E e, F f, G g) throws Exception;

}

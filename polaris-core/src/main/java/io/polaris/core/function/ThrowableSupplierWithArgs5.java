package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ThrowableSupplierWithArgs5<A, B, C, D, E, R> {

	R get(A a, B b, C c, D d, E e) throws Throwable;

}

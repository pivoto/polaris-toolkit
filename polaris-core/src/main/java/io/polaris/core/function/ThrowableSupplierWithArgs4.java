package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ThrowableSupplierWithArgs4<A, B, C, D, R> {

	R get(A a, B b, C c, D d) throws Throwable;

}

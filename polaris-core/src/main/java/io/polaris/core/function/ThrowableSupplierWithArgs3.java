package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ThrowableSupplierWithArgs3<A, B, C, R> {

	R get(A a, B b, C c) throws Throwable;

}

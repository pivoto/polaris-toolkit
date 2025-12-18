package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableSupplierWithArgs4<A, B, C, D, R> {

	R get(A a, B b, C c, D d) throws Throwable;

}

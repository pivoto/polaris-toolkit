package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ThrowableSupplierWithArgs2<A, B,R> {

	R get(A a, B b) throws Throwable;

}

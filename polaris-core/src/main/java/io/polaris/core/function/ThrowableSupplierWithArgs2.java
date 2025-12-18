package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableSupplierWithArgs2<A, B,R> {

	R get(A a, B b) throws Throwable;

}

package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableSupplierWithArgs<T,R> {

	R get(T... args) throws Throwable;

}

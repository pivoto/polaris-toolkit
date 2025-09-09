package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ThrowableSupplierWithArgs<T,R> {

	R get(T... args) throws Throwable;

}

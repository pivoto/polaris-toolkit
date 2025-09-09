package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ThrowableSupplierWithArgs1<T,R> {

	R get(T t) throws Throwable;

}

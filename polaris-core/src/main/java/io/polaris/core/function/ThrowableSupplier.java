package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableSupplier<R> {

	R get() throws Throwable;

}

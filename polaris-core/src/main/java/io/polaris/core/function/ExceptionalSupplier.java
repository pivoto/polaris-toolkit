package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalSupplier<R> {

	R get() throws Exception;

}

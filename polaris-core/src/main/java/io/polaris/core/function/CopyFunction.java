package io.polaris.core.function;

/**
 * @author Qt
 * @since Oct 21, 2025
 */
@FunctionalInterface
public interface CopyFunction<T> {

	T copy(T source);

}

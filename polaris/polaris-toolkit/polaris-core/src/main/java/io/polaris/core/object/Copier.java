package io.polaris.core.object;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface Copier<T> {

	T copy();

}

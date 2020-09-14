package io.polaris.core.lang.copier;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface Copier<T> {

	T copy();

}

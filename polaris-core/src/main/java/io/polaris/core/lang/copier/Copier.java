package io.polaris.core.lang.copier;

/**
 * @author Qt
 * @since 1.8
 */
public interface Copier<T> {

	T copy();

	T deepCopy();
}

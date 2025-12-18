package io.polaris.core.lang.copier;

/**
 * @author Qt
 */
public interface Copier<T> {

	T copy();

	T deepCopy();
}

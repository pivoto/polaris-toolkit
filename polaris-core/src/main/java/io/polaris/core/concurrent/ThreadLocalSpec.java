package io.polaris.core.concurrent;

/**
 * @author Qt
 * @since May 17, 2026
 */
public interface ThreadLocalSpec<T> {

	T get();

	void set(T value);

	void remove();

}

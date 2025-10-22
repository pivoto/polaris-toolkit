package io.polaris.core.concurrent;

import java.util.concurrent.Callable;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public interface WrappingTaskFactory {

	Runnable wrap(Runnable runnable);

	<V> Callable<V> wrap(Callable<V> callable);


	default boolean isWrapping(Runnable runnable) {
		return runnable instanceof WrappingTask;
	}

	default <V> boolean isWrapping(Callable<V> callable) {
		return callable instanceof WrappingTask;
	}

}

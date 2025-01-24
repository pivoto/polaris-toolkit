package io.polaris.core.concurrent;

import java.util.concurrent.Callable;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public interface WrappingTaskFactory {

	WrappingRunnable wrap(Runnable runnable);

	<V> WrappingCallable<V> wrap(Callable<V> callable);

}

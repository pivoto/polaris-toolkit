package io.polaris.core.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public class WrappingFutureTask<V> extends FutureTask<V> {

	public WrappingFutureTask(WrappingTaskFactory wrappedTaskFactory, Callable<V> callable) {
		super(wrappedTaskFactory.wrap(callable));
	}

	public WrappingFutureTask(WrappingTaskFactory wrappedTaskFactory, Runnable runnable, V result) {
		super(wrappedTaskFactory.wrap(runnable), result);
	}
}

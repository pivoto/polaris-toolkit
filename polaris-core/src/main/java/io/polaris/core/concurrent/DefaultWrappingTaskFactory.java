package io.polaris.core.concurrent;

import java.util.concurrent.Callable;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public class DefaultWrappingTaskFactory implements WrappingTaskFactory {

	private WrappingExecutionListener listener;
	private WrappingInterceptor interceptor;

	public DefaultWrappingTaskFactory() {
	}


	@Override
	public Runnable wrap(Runnable runnable) {
		return WrappingTasks.wrap(listener, interceptor, runnable);
	}

	@Override
	public <V> Callable<V> wrap(Callable<V> callable) {
		return WrappingTasks.wrap(listener, interceptor, callable);
	}

	@Override
	@SuppressWarnings("RedundantMethodOverride")
	public boolean isWrapping(Runnable runnable) {
		return runnable instanceof WrappingTask;
	}

	@Override
	@SuppressWarnings("RedundantMethodOverride")
	public <V> boolean isWrapping(Callable<V> callable) {
		return callable instanceof WrappingTask;
	}

	public WrappingExecutionListener getListener() {
		return listener;
	}

	public void setListener(WrappingExecutionListener listener) {
		this.listener = listener;
	}

	public WrappingInterceptor getInterceptor() {
		return interceptor;
	}

	public void setInterceptor(WrappingInterceptor interceptor) {
		this.interceptor = interceptor;
	}

}

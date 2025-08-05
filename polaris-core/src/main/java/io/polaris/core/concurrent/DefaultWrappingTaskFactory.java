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
	public WrappingRunnable wrap(Runnable runnable) {
		return WrappingTasks.wrap(listener, interceptor, runnable);
	}

	@Override
	public <V> WrappingCallable<V> wrap(Callable<V> callable) {
		return WrappingTasks.wrap(listener, interceptor, callable);
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

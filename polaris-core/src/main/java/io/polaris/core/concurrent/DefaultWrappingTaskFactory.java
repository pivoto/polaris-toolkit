package io.polaris.core.concurrent;

import java.util.concurrent.Callable;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.ServiceDefault;
import io.polaris.core.service.SpiLoaders;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
@ServiceDefault
public class DefaultWrappingTaskFactory implements WrappingTaskFactory {
	private static final Logger log = Loggers.of(DefaultWrappingTaskFactory.class);
	private WrappingExecutionListener listener;
	private WrappingInterceptor interceptor;

	public DefaultWrappingTaskFactory() {
		try {
			WrappingInterceptor interceptor = SpiLoaders.load(WrappingInterceptor.class).getPureSingleton();
			if (interceptor != null) {
				setInterceptor(interceptor);
			}
			WrappingExecutionListener listener = SpiLoaders.load(WrappingExecutionListener.class).getPureSingleton();
			if (listener != null) {
				setListener(listener);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
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

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
		return new DefaultWrappingTask<>(listener, interceptor, runnable);
	}

	@Override
	public <V> WrappingCallable<V> wrap(Callable<V> callable) {
		return new DefaultWrappingTask<>(listener, interceptor, callable);
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

	public static class DefaultWrappingTask<V> implements WrappingRunnable, WrappingCallable<V>, WrappingExecutionListener {
		private final WrappingExecutionListener listener;
		private final WrappingInterceptor interceptor;
		private final Runnable runnable;
		private final Callable<V> callable;


		public DefaultWrappingTask(WrappingExecutionListener listener, WrappingInterceptor interceptor, Runnable runnable) {
			this.listener = listener;
			this.interceptor = interceptor;
			if (interceptor != null) {
				this.runnable = () -> {
					try {
						interceptor.onBefore();
						runnable.run();
						interceptor.onAfter();
					} catch (Throwable e) {
						interceptor.onThrowing(e);
					} finally {
						interceptor.onFinally();
					}
				};
			} else {
				this.runnable = runnable;
			}
			this.callable = null;
		}

		public DefaultWrappingTask(WrappingExecutionListener listener, WrappingInterceptor interceptor, Callable<V> callable) {
			this.listener = listener;
			this.interceptor = interceptor;
			if (interceptor != null) {
				this.callable = () -> {
					try {
						interceptor.onBefore();
						V rs = callable.call();
						interceptor.onAfter();
						return rs;
					} catch (Throwable e) {
						interceptor.onThrowing(e);
						throw e;
					} finally {
						interceptor.onFinally();
					}
				};
			} else {
				this.callable = callable;
			}
			this.runnable = null;
		}

		@Override
		public void run() {
			if (runnable != null) {
				runnable.run();
			} else {
				try {
					callable.call();
				} catch (RuntimeException e) {
					throw e;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		@Override
		public void beforeExecute(Thread t, Runnable r) {
			if (listener != null) {
				listener.beforeExecute(t, r);
			}
		}

		@Override
		public void afterExecute(Runnable r, Throwable t) {
			if (listener != null) {
				listener.afterExecute(r, t);
			}
		}

		@Override
		public V call() throws Exception {
			if (callable != null) {
				return callable.call();
			}
			runnable.run();
			return null;
		}
	}

}

package io.polaris.core.concurrent;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public class WrappingTasks {

	public static Runnable withInterceptor(WrappingInterceptor interceptor, @Nonnull Runnable runnable) {
		if (interceptor == null) {
			return runnable;
		}
		return () -> {
			try {
				interceptor.onBefore();
				runnable.run();
				interceptor.onAfter();
			} catch (Throwable e) {
				interceptor.onThrowing(e);
				throw e;
			} finally {
				interceptor.onFinally();
			}
		};
	}

	public static <V> Callable<V> withInterceptor(WrappingInterceptor interceptor, @Nonnull Callable<V> callable) {
		if (interceptor == null) {
			return callable;
		}
		return () -> {
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
	}

	public static WrappingRunnable wrap(WrappingExecutionListener listener, WrappingInterceptor interceptor, @Nonnull Runnable runnable) {
		return new RunnableWrappingTask(listener, interceptor, runnable);
	}

	public static <V> WrappingCallable<V> wrap(WrappingExecutionListener listener, WrappingInterceptor interceptor, @Nonnull Callable<V> callable) {
		return new CallableWrappingTask<>(listener, interceptor, callable);
	}


	public static class RunnableWrappingTask extends BaseWrappingTask implements WrappingRunnable, WrappingCallable<Object> {
		private final Runnable originRunnable;
		private final Runnable runnable;
		private final Callable<Object> callable;

		public RunnableWrappingTask(WrappingExecutionListener listener, WrappingInterceptor interceptor, @Nonnull Runnable runnable) {
			super(listener);
			if (runnable instanceof WrappingRunnable) {
				runnable = ((WrappingRunnable) runnable).getOriginRunnable();
			}
			this.originRunnable = runnable;
			this.runnable = withInterceptor(interceptor, runnable);
			if (runnable instanceof Callable) {
				//noinspection unchecked
				callable = withInterceptor(interceptor, (Callable<Object>) runnable);
			} else {
				callable = null;
			}
		}

		@Override
		@Nonnull
		public Runnable getOriginRunnable() {
			return originRunnable;
		}

		@Nonnull
		@Override
		public Callable<Object> getOriginCallable() {
			if (originRunnable instanceof Callable) {
				//noinspection unchecked
				return (Callable<Object>) originRunnable;
			}
			return () -> {
				originRunnable.run();
				return null;
			};
		}

		@Override
		public void run() {
			runnable.run();
		}

		@Override
		public Object call() throws Exception {
			if (callable != null) {
				return callable.call();
			}
			runnable.run();
			return null;
		}

	}


	public static class CallableWrappingTask<V> extends BaseWrappingTask implements WrappingCallable<V>, WrappingRunnable {
		private final Callable<V> originCallable;
		private final Runnable runnable;
		private final Callable<V> callable;

		public CallableWrappingTask(WrappingExecutionListener listener, WrappingInterceptor interceptor, @Nonnull Callable<V> callable) {
			super(listener);
			if (callable instanceof WrappingCallable) {
				callable = ((WrappingCallable<V>) callable).getOriginCallable();
			}
			this.originCallable = callable;
			this.callable = withInterceptor(interceptor, callable);
			if (callable instanceof Runnable) {
				runnable = withInterceptor(interceptor, (Runnable) callable);
			} else {
				runnable = null;
			}
		}

		@Nonnull
		@Override
		public Callable<V> getOriginCallable() {
			return originCallable;
		}

		@Nonnull
		@Override
		public Runnable getOriginRunnable() {
			if (originCallable instanceof Runnable) {
				return (Runnable) originCallable;
			}
			return () -> {
				try {
					originCallable.call();
				} catch (RuntimeException e) {
					throw e;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			};
		}

		@Override
		public V call() throws Exception {
			return callable.call();
		}

		@Override
		public void run() {
			if (runnable != null) {
				runnable.run();
				return;
			}
			try {
				callable.call();
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	protected static class BaseWrappingTask implements WrappingTask, WrappingExecutionListener {
		private final WrappingExecutionListener listener;

		public BaseWrappingTask(WrappingExecutionListener listener) {
			this.listener = listener;
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

	}


}

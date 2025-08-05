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
		private final Runnable runnable;
		private final Callable<Object> callable;

		public RunnableWrappingTask(WrappingExecutionListener listener, WrappingInterceptor interceptor, @Nonnull Runnable runnable) {
			super(listener);
			this.runnable = withInterceptor(interceptor, runnable);
			if (runnable instanceof Callable) {
				//noinspection unchecked
				callable = withInterceptor(interceptor, (Callable<Object>) runnable);
			} else {
				callable = null;
			}
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
		private final Runnable runnable;
		private final Callable<V> callable;

		public CallableWrappingTask(WrappingExecutionListener listener, WrappingInterceptor interceptor, @Nonnull Callable<V> callable) {
			super(listener);
			this.callable = withInterceptor(interceptor, callable);
			if (callable instanceof Runnable) {
				runnable = withInterceptor(interceptor, (Runnable) callable);
			} else {
				runnable = null;
			}
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

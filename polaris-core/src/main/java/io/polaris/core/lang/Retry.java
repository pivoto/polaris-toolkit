package io.polaris.core.lang;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import io.polaris.core.concurrent.BackoffPolicies;
import io.polaris.core.concurrent.Executors;
import io.polaris.core.concurrent.policy.BackoffPolicy;
import io.polaris.core.err.Exceptions;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;

/**
 * @author Qt
 * @since 1.8
 */
public class Retry {
	private static final Logger log = Loggers.of(Retry.class);

	// region ScheduledExecutorService

	@SafeVarargs
	public static <T> Future<T> doRetry(@Nonnull ScheduledExecutorService executor, @Nonnull Callable<T> callable, int retryCount, long interval,
		Class<? extends Throwable>... retryException) {
		return doRetry(executor, callable, BackoffPolicies.fixedBackoff(retryCount, interval), retryException);
	}

	@SafeVarargs
	public static <T> Future<T> doRetry(@Nonnull ScheduledExecutorService executor, @Nonnull Callable<T> callable, int retryCount, long interval, boolean exponential,
		Class<? extends Throwable>... retryException) {
		BackoffPolicy backoffPolicy = exponential ? BackoffPolicies.exponentialBackoff(retryCount, interval)
			: BackoffPolicies.fixedBackoff(retryCount, interval);
		return doRetry(executor, callable, backoffPolicy, retryException);
	}

	@SafeVarargs
	public static <T> Future<T> doRetry(@Nonnull ScheduledExecutorService executor, @Nonnull Callable<T> callable, BackoffPolicy backoffPolicy,
		Class<? extends Throwable>... retryException) {
		RetryFutureTask<T> retryFutureTask = new RetryFutureTask<>();
		ExecutionFutureTask<T> task = new ExecutionFutureTask<>(callable, executor, backoffPolicy, retryFutureTask, retryException);
		executor.execute(task);
		return retryFutureTask;
	}

	@SafeVarargs
	public static <T> Future<T> doRetry(@Nonnull ScheduledExecutorService executor, @Nonnull Supplier<T> supplier, int retryCount, long interval,
		Class<? extends Throwable>... retryException) {
		Callable<T> callable = Executors.callable(supplier);
		return doRetry(executor, callable, retryCount, interval, retryException);
	}

	@SafeVarargs
	public static <T> Future<T> doRetry(@Nonnull ScheduledExecutorService executor, @Nonnull Supplier<T> supplier, int retryCount, long interval, boolean exponential,
		Class<? extends Throwable>... retryException) {
		Callable<T> callable = Executors.callable(supplier);
		return doRetry(executor, callable, retryCount, interval, exponential, retryException);
	}

	@SafeVarargs
	public static <T> Future<T> doRetry(@Nonnull ScheduledExecutorService executor, @Nonnull Supplier<T> supplier, BackoffPolicy backoffPolicy,
		Class<? extends Throwable>... retryException) {
		Callable<T> callable = Executors.callable(supplier);
		return doRetry(executor, callable, backoffPolicy, retryException);
	}

	@SafeVarargs
	public static Future<?> doRetry(@Nonnull ScheduledExecutorService executor, @Nonnull Runnable runnable, int retryCount, long interval, boolean exponential,
		Class<? extends Throwable>... retryException) {
		BackoffPolicy backoffPolicy = exponential ? BackoffPolicies.exponentialBackoff(retryCount, interval)
			: BackoffPolicies.fixedBackoff(retryCount, interval);
		return doRetry(executor, runnable, backoffPolicy, retryException);
	}

	@SafeVarargs
	public static Future<?> doRetry(@Nonnull ScheduledExecutorService executor, @Nonnull Runnable runnable, int retryCount, long interval, Class<? extends Throwable>... retryException) {
		return doRetry(executor, runnable, BackoffPolicies.fixedBackoff(retryCount, interval), retryException);
	}

	@SafeVarargs
	public static Future<?> doRetry(@Nonnull ScheduledExecutorService executor, @Nonnull Runnable runnable, BackoffPolicy backoffPolicy,
		Class<? extends Throwable>... retryException) {
		Callable<Object> callable = Executors.callable(runnable);
		return doRetry(executor, callable, backoffPolicy, retryException);
	}

	// endregion ScheduledExecutorService

	// region ExecutorService


	@SafeVarargs
	public static <T> Future<T> doRetry(@Nonnull ExecutorService executor, @Nonnull Callable<T> callable, int retryCount,
		Class<? extends Throwable>... retryException) {
		RetryFutureTask<T> retryFutureTask = new RetryFutureTask<>();
		ImmediateExecutionFutureTask<T> task = new ImmediateExecutionFutureTask<>(callable, executor, retryCount, retryFutureTask, retryException);
		executor.execute(task);
		return retryFutureTask;
	}


	@SafeVarargs
	public static <T> Future<T> doRetry(@Nonnull ExecutorService executor, @Nonnull Supplier<T> supplier, int retryCount,
		Class<? extends Throwable>... retryException) {
		Callable<T> callable = Executors.callable(supplier);
		return doRetry(executor, callable, retryCount, retryException);
	}


	@SafeVarargs
	public static Future<?> doRetry(@Nonnull ExecutorService executor, @Nonnull Runnable runnable, int retryCount,
		Class<? extends Throwable>... retryException) {
		Callable<Object> callable = Executors.callable(runnable);
		return doRetry(executor, callable, retryCount, retryException);
	}

	// endregion ScheduledExecutorService

	// region current thread

	@SafeVarargs
	public static <T> T doRetry(@Nonnull Callable<T> callable, int retryCount,
		Class<? extends Throwable>... retryException) throws Exception {
		return doRetry(callable, retryCount, 0, false, retryException);
	}

	@SafeVarargs
	public static <T> T doRetry(@Nonnull Callable<T> callable, int retryCount, long interval,
		Class<? extends Throwable>... retryException) throws Exception {
		return doRetry(callable, retryCount, interval, false, retryException);
	}


	@SafeVarargs
	public static <T> T doRetry(@Nonnull Callable<T> callable, int retryCount, long interval, boolean exponential,
		Class<? extends Throwable>... retryException) throws Exception {
		Throwable saveException = null;
		retryCount = Math.max(retryCount, 0);
		for (int i = 0; i <= retryCount; i++) {
			try {
				return callable.call();
			} catch (Throwable e) {
				saveException = e;
				if (!waitForRetry(i, retryCount, interval, exponential, saveException, retryException)) {
					throw saveException instanceof Exception ?
						(Exception) saveException : new RuntimeException(saveException);
				}
			}
		}
		throw saveException instanceof Exception ?
			(Exception) saveException : new RuntimeException(saveException);
	}

	@SafeVarargs
	public static <T> T doRetry(@Nonnull Supplier<T> supplier, int retryCount,
		Class<? extends Throwable>... retryException) throws Exception {
		return doRetry(supplier, retryCount, 0, false, retryException);
	}

	@SafeVarargs
	public static <T> T doRetry(@Nonnull Supplier<T> supplier, int retryCount, long interval,
		Class<? extends Throwable>... retryException) throws Exception {
		return doRetry(supplier, retryCount, interval, false, retryException);
	}

	@SafeVarargs
	public static <T> T doRetry(@Nonnull Supplier<T> supplier, int retryCount, long interval, boolean exponential,
		Class<? extends Throwable>... retryException) throws Exception {
		Throwable saveException = null;
		retryCount = Math.max(retryCount, 0);
		for (int i = 0; i <= retryCount; i++) {
			try {
				return supplier.get();
			} catch (Throwable e) {
				saveException = e;
				if (!waitForRetry(i, retryCount, interval, exponential, saveException, retryException)) {
					throw saveException instanceof Exception ?
						(Exception) saveException : new RuntimeException(saveException);
				}
			}
		}
		throw saveException instanceof Exception ?
			(Exception) saveException : new RuntimeException(saveException);
	}

	@SafeVarargs
	public static void doRetry(@Nonnull Runnable runnable, int retryCount,
		Class<? extends Throwable>... retryException) {
		doRetry(runnable, retryCount, 0, false, retryException);
	}

	@SafeVarargs
	public static void doRetry(@Nonnull Runnable runnable, int retryCount, long interval,
		Class<? extends Throwable>... retryException) {
		doRetry(runnable, retryCount, interval, false, retryException);
	}


	@SafeVarargs
	public static void doRetry(@Nonnull Runnable runnable, int retryCount, long interval, boolean exponential,
		Class<? extends Throwable>... retryException) {
		Throwable saveException = null;
		retryCount = Math.max(retryCount, 0);
		for (int i = 0; i <= retryCount; i++) {
			try {
				runnable.run();
				return;
			} catch (Throwable e) {
				saveException = e;
				if (!waitForRetry(i, retryCount, interval, exponential, saveException, retryException)) {
					throw saveException instanceof RuntimeException ?
						(RuntimeException) saveException : new RuntimeException(saveException);
				}
			}
		}
		throw saveException instanceof RuntimeException ?
			(RuntimeException) saveException : new RuntimeException(saveException);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean waitForRetry(int attempts, int retryCount, long interval, boolean exponential, Throwable exception, Class<? extends Throwable>[] retryException) {
		boolean retryable = isRetryable(exception, retryException);
		if (!retryable) {
			return false;
		}
		if (attempts < retryCount) {
			if (interval > 0) {
				long sleepMills = interval;
				if (exponential) {
					sleepMills = interval * (long) Math.pow(2, attempts);
				}
				log.info("程序执行过程失败! 预计在{}ms后第{}次重试", sleepMills, attempts + 1);
				try {
					Thread.sleep(sleepMills);
				} catch (InterruptedException ignored) {
				}
			} else {
				log.info("程序执行过程失败! 第{}次重试", attempts + 1);
			}
		}
		return true;
	}

	// endregion current thread


	/**
	 * 判断异常是否可重试
	 *
	 * @param exception       需要判断的异常
	 * @param retryExceptions 可重试的异常类型数组，如果为空或长度为0，则认为所有异常都可重试
	 * @return 如果异常可重试返回true，否则返回false
	 */
	private static boolean isRetryable(Throwable exception, Class<? extends Throwable>[] retryExceptions) {
		return Exceptions.matches(exception, retryExceptions);
	}


	static class RetryFutureTask<T> extends FutureTask<T> {

		public RetryFutureTask() {
			super(() -> {
				throw new UnsupportedOperationException();
			});
		}

		@Override
		public final void run() {
			// 不作任何处理
		}

		@Override
		public final void set(T t) {
			super.set(t);
		}

		@Override
		public final void setException(Throwable t) {
			super.setException(t);
		}

	}

	static class ImmediateExecutionFutureTask<T> extends FutureTask<T> {
		private final Callable<T> callable;
		private final ExecutorService executor;
		private final int attempts;
		private final int retryCount;
		private final RetryFutureTask<T> retryFutureTask;
		private final Class<? extends Throwable>[] retryException;

		private ImmediateExecutionFutureTask(Callable<T> callable, ExecutorService executor, int attempts, int retryCount, RetryFutureTask<T> retryFutureTask, Class<? extends Throwable>[] retryException) {
			super(callable);
			this.callable = callable;
			this.executor = executor;
			this.attempts = attempts;
			this.retryCount = retryCount;
			this.retryFutureTask = retryFutureTask;
			this.retryException = retryException;
		}

		public ImmediateExecutionFutureTask(Callable<T> callable, ExecutorService executor, int retryCount, RetryFutureTask<T> retryFutureTask, Class<? extends Throwable>[] retryException) {
			this(callable, executor, 0, retryCount, retryFutureTask, retryException);
		}

		@Override
		public final void run() {
			super.run();
		}

		@Override
		protected final void set(T t) {
			retryFutureTask.set(t);
			super.set(t);
		}

		@Override
		protected final void setException(Throwable t) {
			super.setException(t);
			if (!isRetryable(t, retryException)) {
				retryFutureTask.setException(t);
				return;
			}

			if (attempts >= retryCount) { // 重试次数已用完
				retryFutureTask.setException(t);
				return;
			}
			int nextAttempts = attempts + 1;
			ImmediateExecutionFutureTask<T> task = new ImmediateExecutionFutureTask<>(callable, executor, nextAttempts, retryCount, retryFutureTask, retryException);
			log.info("程序执行过程失败! 第{}次重试", nextAttempts);
			executor.execute(task);
		}

	}

	static class ExecutionFutureTask<T> extends FutureTask<T> {
		private final Callable<T> callable;
		private final ScheduledExecutorService executor;
		private final int attempts;
		private final BackoffPolicy backoffPolicy;
		private final RetryFutureTask<T> retryFutureTask;
		private final Class<? extends Throwable>[] retryException;

		private ExecutionFutureTask(Callable<T> callable, ScheduledExecutorService executor, int attempts, BackoffPolicy backoffPolicy, RetryFutureTask<T> retryFutureTask, Class<? extends Throwable>[] retryException) {
			super(callable);
			this.callable = callable;
			this.executor = executor;
			this.attempts = attempts;
			this.backoffPolicy = backoffPolicy;
			this.retryFutureTask = retryFutureTask;
			this.retryException = retryException;
		}

		public ExecutionFutureTask(Callable<T> callable, ScheduledExecutorService executor, BackoffPolicy backoffPolicy, RetryFutureTask<T> retryFutureTask, Class<? extends Throwable>[] retryException) {
			this(callable, executor, 0, backoffPolicy, retryFutureTask, retryException);
		}

		@Override
		public final void run() {
			super.run();
		}

		@Override
		protected final void set(T t) {
			retryFutureTask.set(t);
			super.set(t);
		}

		@Override
		protected final void setException(Throwable t) {
			super.setException(t);
			if (!isRetryable(t, retryException)) {
				retryFutureTask.setException(t);
				return;
			}

			long next = 0;
			try {
				if (!backoffPolicy.hasNext()) {
					retryFutureTask.setException(t);
					return;
				}
				next = backoffPolicy.next();
			} catch (Throwable e) {
				log.error("获取重试时间失败", e);
				retryFutureTask.setException(t);
				return;
			}
			int nextAttempts = attempts + 1;
			ExecutionFutureTask<T> task = new ExecutionFutureTask<>(callable, executor, nextAttempts, backoffPolicy, retryFutureTask, retryException);
			if (next <= 0) {
				log.info("程序执行过程失败! 第{}次重试", nextAttempts);
				executor.execute(task);
			} else {
				log.info("程序执行过程失败! 预计在{}ms后第{}次重试", next, nextAttempts);
				executor.schedule(task, next, TimeUnit.MILLISECONDS);
			}
		}

	}
}

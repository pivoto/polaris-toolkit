package io.polaris.core.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class Executors {

	public static final int KEEP_ALIVE_TIME = 30000;
	public static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MILLISECONDS;
	private static int defaultKeepAliveTime = KEEP_ALIVE_TIME;
	private static TimeUnit defaultKeepAliveTimeUnit = KEEP_ALIVE_TIME_UNIT;
	private static RejectedExecutionHandler defaultRejectedPolicy = new ThreadPoolExecutor.CallerRunsPolicy();

	public static int getDefaultKeepAliveTime() {
		return defaultKeepAliveTime;
	}

	public static void setDefaultKeepAliveTime(int defaultKeepAliveTime) {
		Executors.defaultKeepAliveTime = defaultKeepAliveTime;
	}

	public static TimeUnit getDefaultKeepAliveTimeUnit() {
		return defaultKeepAliveTimeUnit;
	}

	public static void setDefaultKeepAliveTimeUnit(TimeUnit defaultKeepAliveTimeUnit) {
		Executors.defaultKeepAliveTimeUnit = defaultKeepAliveTimeUnit;
	}

	public static void setDefaultRejectedPolicy(RejectedExecutionHandler defaultRejectedPolicy) {
		Executors.defaultRejectedPolicy = defaultRejectedPolicy;
	}

	public static RejectedExecutionHandler getDefaultRejectedPolicy() {
		return defaultRejectedPolicy;
	}

	static BlockingQueue<Runnable> createDefaultBlockingQueue() {
		return new LinkedBlockingQueue<>(1000);
	}

	public static ThreadPoolExecutor create(int core, final String threadNamePrefix) {
		return create((WrappingTaskFactory) null, core, core, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, int max, final String threadNamePrefix) {
		return create((WrappingTaskFactory) null, core, max, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, final String threadNamePrefix, final boolean isDaemon) {
		return create((WrappingTaskFactory) null, core, core, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, int max, final String threadNamePrefix, final boolean isDaemon) {
		return create((WrappingTaskFactory) null, core, max, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return create((WrappingTaskFactory) null, core, core, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}

	public static ThreadPoolExecutor create(int core, int max, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return create((WrappingTaskFactory) null, core, max, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}


	public static ThreadPoolExecutor create(int core, ThreadFactory threadFactory) {
		return create((WrappingTaskFactory) null, core, core, createDefaultBlockingQueue(), threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, ThreadFactory threadFactory) {
		return create((WrappingTaskFactory) null, core, max, createDefaultBlockingQueue(), threadFactory);
	}


	public static ThreadPoolExecutor create(int core, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create((WrappingTaskFactory) null, core, core, blockingQueue, threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create((WrappingTaskFactory) null, core, max, blockingQueue, threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create((WrappingTaskFactory) null, core, max, keepAliveTime, unit, blockingQueue, threadFactory);
	}


	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, final String threadNamePrefix) {
		return create(wrappingTaskFactory, core, core, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, int max, final String threadNamePrefix) {
		return create(wrappingTaskFactory, core, max, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, final String threadNamePrefix, final boolean isDaemon) {
		return create(wrappingTaskFactory, core, core, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, int max, final String threadNamePrefix, final boolean isDaemon) {
		return create(wrappingTaskFactory, core, max, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return create(wrappingTaskFactory, core, core, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, int max, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return create(wrappingTaskFactory, core, max, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}


	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, ThreadFactory threadFactory) {
		return create(wrappingTaskFactory, core, core, createDefaultBlockingQueue(), threadFactory);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, int max, ThreadFactory threadFactory) {
		return create(wrappingTaskFactory, core, max, createDefaultBlockingQueue(), threadFactory);
	}


	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create(wrappingTaskFactory, core, core, blockingQueue, threadFactory);
	}


	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, int max, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create(wrappingTaskFactory, core, max, defaultKeepAliveTime, defaultKeepAliveTimeUnit, blockingQueue, threadFactory);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, int max, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		if (wrappingTaskFactory != null) {
			WrappingThreadPoolExecutor executor = new WrappingThreadPoolExecutor(core, max, keepAliveTime, unit, blockingQueue, threadFactory, defaultRejectedPolicy);
			executor.setWrappedTaskFactory(wrappingTaskFactory);
			return executor;
		}
		return new ThreadPoolExecutor(core, max, keepAliveTime, unit, blockingQueue, threadFactory, defaultRejectedPolicy);
	}


	public static void shutdown(ExecutorService pool) {
		shutdown(pool, 60);
	}

	public static void shutdown(ExecutorService pool, int timeoutSeconds) {
		if (pool == null) {
			return;
		}
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
					System.err.println("Pool did not terminate");
				}
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	public static Runnable ignoreThrowable(Runnable runnable) {
		return () -> {
			try {
				runnable.run();
			} catch (Throwable ignored) {
			}
		};
	}

	public static Runnable ignoreThrowable(ThrowableRunnable runnable) {
		return () -> {
			try {
				runnable.run();
			} catch (Throwable ignored) {
			}
		};
	}

	public static <V> Supplier<V> ignoreThrowable(Callable<V> callable) {
		return () -> {
			try {
				return callable.call();
			} catch (Throwable ignored) {
				return null;
			}
		};
	}

	public static interface ThrowableRunnable {
		void run() throws Throwable;
	}
}

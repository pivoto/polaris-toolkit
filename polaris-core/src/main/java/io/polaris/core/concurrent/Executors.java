package io.polaris.core.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import io.polaris.core.concurrent.queue.MemorySafeLinkedBlockingQueue;

/**
 * @author Qt
 * @since 1.8
 */
public class Executors {

	public static final int KEEP_ALIVE_TIME = 30000;
	public static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MILLISECONDS;
	private static int defaultKeepAliveTime = KEEP_ALIVE_TIME;
	private static TimeUnit defaultKeepAliveTimeUnit = KEEP_ALIVE_TIME_UNIT;
	private static RejectedExecutionHandler defaultRejectedPolicy = RejectedPolicies.CALLER_RUNS.policy();
	private static int defaultQueueSize = 1000;

	// region default param

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

	public static int getDefaultQueueSize() {
		return defaultQueueSize;
	}

	public static void setDefaultQueueSize(int defaultQueueSize) {
		Executors.defaultQueueSize = defaultQueueSize;
	}

	// endregion


	static BlockingQueue<Runnable> createDefaultBlockingQueue() {
		return createBlockingQueue(defaultQueueSize);
	}

	public static BlockingQueue<Runnable> createBlockingQueue(int queueSize) {
		if (queueSize > 0) {
			return new LinkedBlockingQueue<>(queueSize);
		}
		if (queueSize == 0) {
			return new SynchronousQueue<>();
		}
		return new MemorySafeLinkedBlockingQueue<>();
	}

	public static ThreadPoolExecutor create(int core, String threadNamePrefix) {
		return create((WrappingTaskFactory) null, core, core, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, int max, String threadNamePrefix) {
		return create((WrappingTaskFactory) null, core, max, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, String threadNamePrefix, boolean isDaemon) {
		return create((WrappingTaskFactory) null, core, core, threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, String threadNamePrefix, int queueSize, boolean isDaemon) {
		return create((WrappingTaskFactory) null, core, threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, int max, String threadNamePrefix, boolean isDaemon) {
		return create((WrappingTaskFactory) null, core, max, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, BlockingQueue<Runnable> blockingQueue, String threadNamePrefix, boolean isDaemon) {
		return create((WrappingTaskFactory) null, core, core, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}

	public static ThreadPoolExecutor create(int core, int max, BlockingQueue<Runnable> blockingQueue, String threadNamePrefix, boolean isDaemon) {
		return create((WrappingTaskFactory) null, core, max, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}


	public static ThreadPoolExecutor create(int core, ThreadFactory threadFactory) {
		return create((WrappingTaskFactory) null, core, threadFactory);
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


	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, String threadNamePrefix) {
		return create(wrappingTaskFactory, core, core, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, int max, String threadNamePrefix) {
		return create(wrappingTaskFactory, core, max, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, String threadNamePrefix, boolean isDaemon) {
		return create(wrappingTaskFactory, core, core, threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, int max, String threadNamePrefix, boolean isDaemon) {
		return create(wrappingTaskFactory, core, max, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, BlockingQueue<Runnable> blockingQueue, String threadNamePrefix, boolean isDaemon) {
		return create(wrappingTaskFactory, core, core, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}

	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, int max, BlockingQueue<Runnable> blockingQueue, String threadNamePrefix, boolean isDaemon) {
		return create(wrappingTaskFactory, core, max, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}


	public static ThreadPoolExecutor create(WrappingTaskFactory wrappingTaskFactory, int core, ThreadFactory threadFactory) {
		return create(wrappingTaskFactory, core, core, threadFactory);
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

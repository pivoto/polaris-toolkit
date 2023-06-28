package io.polaris.core.concurrent;

import java.util.concurrent.*;

/**
 * @author Qt
 * @since 1.8
 */
public class Executors {

	public static final int KEEP_ALIVE_TIME = 30000;
	public static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MILLISECONDS;
	private static RejectedExecutionHandler defaultRejectedPolicy = new ThreadPoolExecutor.CallerRunsPolicy();

	public static void setDefaultRejectedPolicy(RejectedExecutionHandler defaultRejectedPolicy) {
		Executors.defaultRejectedPolicy = defaultRejectedPolicy;
	}

	public static RejectedExecutionHandler getDefaultRejectedPolicy() {
		return defaultRejectedPolicy;
	}

	public static ThreadPoolExecutor create(int core, final String threadNamePrefix) {
		return create(core, core, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, int max, final String threadNamePrefix) {
		return create(core, max, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, final String threadNamePrefix, final boolean isDaemon) {
		return create(core, core, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, int max, final String threadNamePrefix, final boolean isDaemon) {
		return create(core, max, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return new ThreadPoolExecutor(core, core, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, blockingQueue,
			new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}

	public static ThreadPoolExecutor create(int core, int max, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return new ThreadPoolExecutor(core, max, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, blockingQueue,
			new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}


	public static ThreadPoolExecutor create(int core, ThreadFactory threadFactory) {
		return create(core, core, createDefaultBlockingQueue(), threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, ThreadFactory threadFactory) {
		return create(core, max, createDefaultBlockingQueue(), threadFactory);
	}

	private static BlockingQueue<Runnable> createDefaultBlockingQueue() {
		return new LinkedBlockingQueue<>(1000);
	}

	public static ThreadPoolExecutor create(int core, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return new ThreadPoolExecutor(core, core, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, blockingQueue, threadFactory, defaultRejectedPolicy);
	}

	public static ThreadPoolExecutor create(int core, int max, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return new ThreadPoolExecutor(core, max, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, blockingQueue, threadFactory, defaultRejectedPolicy);
	}


	public static void shutdown(ExecutorService pool) {
		if (pool == null) {
			return;
		}
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
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
}

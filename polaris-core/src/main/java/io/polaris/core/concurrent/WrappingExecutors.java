package io.polaris.core.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public class WrappingExecutors extends Executors {
	private static WrappingTaskFactory defaultWrappingTaskFactory;

	public static WrappingTaskFactory getDefaultWrappingTaskFactory() {
		return defaultWrappingTaskFactory;
	}

	public static void setDefaultWrappingTaskFactory(WrappingTaskFactory defaultWrappingTaskFactory) {
		WrappingExecutors.defaultWrappingTaskFactory = defaultWrappingTaskFactory;
	}


	public static ThreadPoolExecutor create(int core, final String threadNamePrefix) {
		return create(defaultWrappingTaskFactory, core, core, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, int max, final String threadNamePrefix) {
		return create(defaultWrappingTaskFactory, core, max, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, final String threadNamePrefix, final boolean isDaemon) {
		return create(defaultWrappingTaskFactory, core, core, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, int max, final String threadNamePrefix, final boolean isDaemon) {
		return create(defaultWrappingTaskFactory, core, max, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return create(defaultWrappingTaskFactory, core, core, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}

	public static ThreadPoolExecutor create(int core, int max, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return create(defaultWrappingTaskFactory, core, max, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}


	public static ThreadPoolExecutor create(int core, ThreadFactory threadFactory) {
		return create(defaultWrappingTaskFactory, core, core, createDefaultBlockingQueue(), threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, ThreadFactory threadFactory) {
		return create(defaultWrappingTaskFactory, core, max, createDefaultBlockingQueue(), threadFactory);
	}

	public static ThreadPoolExecutor create(int core, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create(defaultWrappingTaskFactory, core, core, blockingQueue, threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create(defaultWrappingTaskFactory, core, max, blockingQueue, threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create(defaultWrappingTaskFactory, core, max, keepAliveTime, unit, blockingQueue, threadFactory);
	}

}

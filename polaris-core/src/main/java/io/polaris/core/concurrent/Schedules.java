package io.polaris.core.concurrent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Qt
 * @since 1.8
 */
public class Schedules {

	public static ScheduledExecutorService create(int core) {
		return create((WrappingTaskFactory) null, core);
	}

	public static ScheduledExecutorService create(int core, final String threadNamePrefix) {
		return create((WrappingTaskFactory) null, core, threadNamePrefix, true);
	}

	public static ScheduledExecutorService create(int core, final String threadNamePrefix, final boolean isDaemon) {
		return create((WrappingTaskFactory) null, core, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon));
	}

	public static ScheduledExecutorService create(int core, ThreadFactory threadFactory) {
		return create((WrappingTaskFactory) null, core, threadFactory);
	}

	public static ScheduledExecutorService single(WrappingTaskFactory wrappingTaskFactory) {
		if (wrappingTaskFactory != null) {
			WrappingScheduledThreadPoolExecutor executor = new WrappingScheduledThreadPoolExecutor(1);
			executor.setWrappedTaskFactory(wrappingTaskFactory);
			return executor;
		}
		return new ScheduledThreadPoolExecutor(1);
	}

	public static ScheduledExecutorService create(WrappingTaskFactory wrappingTaskFactory, int core) {
		if (wrappingTaskFactory != null) {
			WrappingScheduledThreadPoolExecutor executor = new WrappingScheduledThreadPoolExecutor(core);
			executor.setWrappedTaskFactory(wrappingTaskFactory);
			return executor;
		}
		return new ScheduledThreadPoolExecutor(core);
	}

	public static ScheduledExecutorService create(WrappingTaskFactory wrappingTaskFactory, int core, final String threadNamePrefix, final boolean isDaemon) {
		return create(wrappingTaskFactory, core, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon));
	}

	public static ScheduledExecutorService create(WrappingTaskFactory wrappingTaskFactory, int core, ThreadFactory threadFactory) {
		if (wrappingTaskFactory != null) {
			WrappingScheduledThreadPoolExecutor executor = new WrappingScheduledThreadPoolExecutor(core, threadFactory);
			executor.setWrappedTaskFactory(wrappingTaskFactory);
			return executor;
		}
		return new ScheduledThreadPoolExecutor(core, threadFactory);
	}


	public static void shutdown(ScheduledExecutorService pool) {
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

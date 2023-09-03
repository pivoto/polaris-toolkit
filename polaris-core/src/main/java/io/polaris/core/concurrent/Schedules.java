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

	public static ScheduledExecutorService single() {
		return new ScheduledThreadPoolExecutor(1);
	}

	public static ScheduledExecutorService create(int core) {
		return new ScheduledThreadPoolExecutor(core);
	}

	public static ScheduledExecutorService create(int core, final String threadNamePrefix) {
		return create(core, threadNamePrefix, true);
	}

	public static ScheduledExecutorService create(int core, final String threadNamePrefix, final boolean isDaemon) {
		return create(core, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon));
	}

	public static ScheduledExecutorService create(int core, ThreadFactory threadFactory) {
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

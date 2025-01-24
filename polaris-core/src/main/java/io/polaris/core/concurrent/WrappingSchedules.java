package io.polaris.core.concurrent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public class WrappingSchedules extends Schedules{
	private static WrappingTaskFactory defaultWrappingTaskFactory;

	public static WrappingTaskFactory getDefaultWrappingTaskFactory() {
		return defaultWrappingTaskFactory;
	}

	public static void setDefaultWrappingTaskFactory(WrappingTaskFactory defaultWrappingTaskFactory) {
		WrappingSchedules.defaultWrappingTaskFactory = defaultWrappingTaskFactory;
	}

	public static ScheduledExecutorService single() {
		return single(defaultWrappingTaskFactory);
	}

	public static ScheduledExecutorService create(int core) {
		return create(defaultWrappingTaskFactory, core);
	}

	public static ScheduledExecutorService create(int core, final String threadNamePrefix) {
		return create(defaultWrappingTaskFactory, core, threadNamePrefix, true);
	}

	public static ScheduledExecutorService create(int core, final String threadNamePrefix, final boolean isDaemon) {
		return create(defaultWrappingTaskFactory, core, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon));
	}

	public static ScheduledExecutorService create(int core, ThreadFactory threadFactory) {
		return create(defaultWrappingTaskFactory, core, threadFactory);
	}

}

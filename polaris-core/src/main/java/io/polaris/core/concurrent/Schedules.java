package io.polaris.core.concurrent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @author Qt
 * @since 1.8
 */
public class Schedules {

	public static ScheduledExecutorService single() {
		return single((WrappingTaskFactory) null);
	}

	public static ScheduledExecutorService defaults() {
		return defaults((WrappingTaskFactory) null);
	}

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
		return create(wrappingTaskFactory, 1);
	}

	public static ScheduledExecutorService defaults(WrappingTaskFactory wrappingTaskFactory) {
		return create(wrappingTaskFactory, Runtime.getRuntime().availableProcessors());
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
		shutdown(pool, 60);
	}

	public static void shutdown(ScheduledExecutorService pool, int timeoutSeconds) {
		Executors.shutdown(pool, timeoutSeconds);
	}

}

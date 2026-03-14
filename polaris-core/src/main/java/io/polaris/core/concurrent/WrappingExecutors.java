package io.polaris.core.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import io.polaris.core.spi.SpiLoader;
import io.polaris.core.spi.SpiLoaders;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public class WrappingExecutors extends Executors {
	private static WrappingTaskFactory defaultWrappingTaskFactory;

	@Nonnull
	public static WrappingTaskFactory buildDefaultWrappingTaskFactory() {
		try {
			SpiLoader<WrappingTaskFactory> loader = SpiLoaders.load(WrappingTaskFactory.class);
			WrappingTaskFactory factory = loader.getSingleton();
			if (factory != null) {
				return factory;
			}
		} catch (Exception ignored) {
		}
		return new DefaultWrappingTaskFactory();
	}

	public static WrappingTaskFactory getDefaultWrappingTaskFactory() {
		if (defaultWrappingTaskFactory == null) {
			defaultWrappingTaskFactory = buildDefaultWrappingTaskFactory();
		}
		return defaultWrappingTaskFactory;
	}

	public static void setDefaultWrappingTaskFactory(WrappingTaskFactory defaultWrappingTaskFactory) {
		WrappingExecutors.defaultWrappingTaskFactory = defaultWrappingTaskFactory;
	}


	public static ThreadPoolExecutor create(int core, final String threadNamePrefix) {
		return create(getDefaultWrappingTaskFactory(), core, core, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, int max, final String threadNamePrefix) {
		return create(getDefaultWrappingTaskFactory(), core, max, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, final String threadNamePrefix, final boolean isDaemon) {
		return create(getDefaultWrappingTaskFactory(), core, core, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, int max, final String threadNamePrefix, final boolean isDaemon) {
		return create(getDefaultWrappingTaskFactory(), core, max, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return create(getDefaultWrappingTaskFactory(), core, core, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}

	public static ThreadPoolExecutor create(int core, int max, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return create(getDefaultWrappingTaskFactory(), core, max, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}


	public static ThreadPoolExecutor create(int core, ThreadFactory threadFactory) {
		return create(getDefaultWrappingTaskFactory(), core, core, createDefaultBlockingQueue(), threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, ThreadFactory threadFactory) {
		return create(getDefaultWrappingTaskFactory(), core, max, createDefaultBlockingQueue(), threadFactory);
	}

	public static ThreadPoolExecutor create(int core, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create(getDefaultWrappingTaskFactory(), core, core, blockingQueue, threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create(getDefaultWrappingTaskFactory(), core, max, blockingQueue, threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create(getDefaultWrappingTaskFactory(), core, max, keepAliveTime, unit, blockingQueue, threadFactory);
	}

}

package io.polaris.core.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import io.polaris.core.service.ServiceLoader;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public class WrappingExecutors extends Executors {
	private static WrappingTaskFactory defaultWrappingTaskFactory;

	@Nonnull
	public static DefaultWrappingTaskFactory buildDefaultWrappingTaskFactory() {
		DefaultWrappingTaskFactory factory = new DefaultWrappingTaskFactory();
		WrappingInterceptor interceptor = ServiceLoader.of(WrappingInterceptor.class).getPureSingleton();
		if (interceptor != null) {
			factory.setInterceptor(interceptor);
		}
		WrappingExecutionListener listener = ServiceLoader.of(WrappingExecutionListener.class).getPureSingleton();
		if (listener != null) {
			factory.setListener(listener);
		}
		return factory;
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
		return create(getDefaultWrappingTaskFactory() , core, core, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, int max, final String threadNamePrefix) {
		return create(getDefaultWrappingTaskFactory() , core, max, threadNamePrefix, true);
	}

	public static ThreadPoolExecutor create(int core, final String threadNamePrefix, final boolean isDaemon) {
		return create(getDefaultWrappingTaskFactory() , core, core, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, int max, final String threadNamePrefix, final boolean isDaemon) {
		return create(getDefaultWrappingTaskFactory() , core, max, createDefaultBlockingQueue(), threadNamePrefix, isDaemon);
	}

	public static ThreadPoolExecutor create(int core, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return create(getDefaultWrappingTaskFactory() , core, core, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}

	public static ThreadPoolExecutor create(int core, int max, BlockingQueue<Runnable> blockingQueue, final String threadNamePrefix, final boolean isDaemon) {
		return create(getDefaultWrappingTaskFactory() , core, max, blockingQueue, new PooledThreadFactory().withPrefix(threadNamePrefix).withDaemon(isDaemon)
		);
	}


	public static ThreadPoolExecutor create(int core, ThreadFactory threadFactory) {
		return create(getDefaultWrappingTaskFactory() , core, core, createDefaultBlockingQueue(), threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, ThreadFactory threadFactory) {
		return create(getDefaultWrappingTaskFactory() , core, max, createDefaultBlockingQueue(), threadFactory);
	}

	public static ThreadPoolExecutor create(int core, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create(getDefaultWrappingTaskFactory() , core, core, blockingQueue, threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create(getDefaultWrappingTaskFactory() , core, max, blockingQueue, threadFactory);
	}

	public static ThreadPoolExecutor create(int core, int max, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
		return create(getDefaultWrappingTaskFactory() , core, max, keepAliveTime, unit, blockingQueue, threadFactory);
	}

}

package io.polaris.core.concurrent.async.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import io.polaris.core.concurrent.*;
import io.polaris.core.concurrent.async.AsyncThreadPoolFactory;
import io.polaris.core.consts.StdKeys;
import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.service.ServiceDefault;
import io.polaris.core.service.ServiceLoader;

/**
 * @author Qt
 * @since Aug 07, 2025
 */
@ServiceDefault
public class DefaultAsyncThreadPoolFactory implements AsyncThreadPoolFactory {

	@Override
	public ExecutorService buildExecutor() {
		int maxPoolSize = GlobalStdEnv.getInt(StdKeys.ASYNC_POOL_MAX);
		if (maxPoolSize < 1) {
			maxPoolSize = Integer.max(8, Runtime.getRuntime().availableProcessors() * 4);
		}
		int corePoolSize = GlobalStdEnv.getInt(StdKeys.ASYNC_POOL_CORE);
		if (corePoolSize < 1) {
			corePoolSize = Integer.min(maxPoolSize, Runtime.getRuntime().availableProcessors());
		}
		WrappingTaskFactory wrappingTaskFactory = WrappingExecutors.getDefaultWrappingTaskFactory();
		WrappingThreadPoolExecutor executor = new WrappingThreadPoolExecutor(corePoolSize, maxPoolSize, 30, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(), GroupThreadFactory.newInstance("AsyncRunner"), RejectedPolicies.CALLER_RUNS.policy());
		executor.allowCoreThreadTimeOut(true);
		executor.setWrappedTaskFactory(wrappingTaskFactory);
		return executor;
	}

	@Override
	public boolean canShutdownExecutor() {
		return true;
	}

	@Override
	public ScheduledExecutorService buildScheduledExecutor() {
		int poolSize = GlobalStdEnv.getInt(StdKeys.ASYNC_POOL_SCHEDULER);
		if (poolSize < 1) {
			poolSize = GlobalStdEnv.getInt(StdKeys.ASYNC_POOL_CORE);
		}
		if (poolSize < 1) {
			poolSize = Runtime.getRuntime().availableProcessors();
		}
		WrappingTaskFactory wrappingTaskFactory = WrappingExecutors.getDefaultWrappingTaskFactory();
		WrappingScheduledThreadPoolExecutor scheduler = new WrappingScheduledThreadPoolExecutor(poolSize, new PooledThreadFactory().withPrefix("ScheduledAsyncRunner").withDaemon(true));
		//scheduler.allowCoreThreadTimeOut(true);// cannot set
		scheduler.setWrappedTaskFactory(wrappingTaskFactory);
		return scheduler;
	}

	@Override
	public boolean canShutdownScheduledExecutor() {
		return true;
	}

}

package io.polaris.core.concurrent.async.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.polaris.core.concurrent.GroupThreadFactory;
import io.polaris.core.concurrent.PooledThreadFactory;
import io.polaris.core.concurrent.RejectedPolicies;
import io.polaris.core.concurrent.WrappingExecutors;
import io.polaris.core.concurrent.WrappingScheduledThreadPoolExecutor;
import io.polaris.core.concurrent.WrappingTaskFactory;
import io.polaris.core.concurrent.WrappingThreadPoolExecutor;
import io.polaris.core.concurrent.async.AsyncThreadPoolFactory;
import io.polaris.core.env.Config;
import io.polaris.core.service.SpiDefault;

/**
 * @author Qt
 * @since Aug 07, 2025
 */
@SpiDefault
public class DefaultAsyncThreadPoolFactory implements AsyncThreadPoolFactory {

	@Override
	public ExecutorService buildExecutor() {
		int maxPoolSize = Config.INSTANCE.async().pool().max();
		if (maxPoolSize < 1) {
			maxPoolSize = Integer.max(8, Runtime.getRuntime().availableProcessors() * 4);
		}
		int corePoolSize = Config.INSTANCE.async().pool().core();
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
		int poolSize = Config.INSTANCE.async().pool().scheduler();
		if (poolSize < 1) {
			poolSize = Config.INSTANCE.async().pool().core();
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

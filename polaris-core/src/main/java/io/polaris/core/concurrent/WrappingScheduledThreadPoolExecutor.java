package io.polaris.core.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
@SuppressWarnings({"DataFlowIssue", "NullableProblems"})
public class WrappingScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
	private WrappingTaskFactory wrappedTaskFactory;


	public WrappingScheduledThreadPoolExecutor(int corePoolSize) {
		super(corePoolSize);
	}

	public WrappingScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
		super(corePoolSize, threadFactory);
	}

	public WrappingScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
		super(corePoolSize, handler);
	}

	public WrappingScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, threadFactory, handler);
	}


	public WrappingTaskFactory getWrappedTaskFactory() {
		return wrappedTaskFactory;
	}

	public void setWrappedTaskFactory(WrappingTaskFactory wrappedTaskFactory) {
		this.wrappedTaskFactory = wrappedTaskFactory;
	}


	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		if (command != null && wrappedTaskFactory != null && !wrappedTaskFactory.isWrapping(command)) {
			command = wrappedTaskFactory.wrap(command);
		}
		return super.schedule(command, delay, unit);
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		if (callable != null && wrappedTaskFactory != null && !wrappedTaskFactory.isWrapping(callable)) {
			callable = wrappedTaskFactory.wrap(callable);
		}
		return super.schedule(callable, delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		if (command != null && wrappedTaskFactory != null && !wrappedTaskFactory.isWrapping(command)) {
			command = wrappedTaskFactory.wrap(command);
		}
		return super.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		if (command != null && wrappedTaskFactory != null && !wrappedTaskFactory.isWrapping(command)) {
			command = wrappedTaskFactory.wrap(command);
		}
		return super.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

	@Override
	public void execute(Runnable command) {
		if (command != null && wrappedTaskFactory != null && !wrappedTaskFactory.isWrapping(command)) {
			command = wrappedTaskFactory.wrap(command);
		}
		super.execute(command);
	}

	@Override
	protected <V> RunnableScheduledFuture<V> decorateTask(
		Runnable runnable, RunnableScheduledFuture<V> task) {
		return task;
	}

	@Override
	protected <V> RunnableScheduledFuture<V> decorateTask(
		Callable<V> callable, RunnableScheduledFuture<V> task) {
		return task;
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		if (runnable != null && wrappedTaskFactory != null && !wrappedTaskFactory.isWrapping(runnable)) {
			runnable = wrappedTaskFactory.wrap(runnable);
		}
		return new FutureTask<T>(runnable, value);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		if (callable != null && wrappedTaskFactory != null && !wrappedTaskFactory.isWrapping(callable)) {
			callable = wrappedTaskFactory.wrap(callable);
		}
		return new FutureTask<T>(callable);
	}
}

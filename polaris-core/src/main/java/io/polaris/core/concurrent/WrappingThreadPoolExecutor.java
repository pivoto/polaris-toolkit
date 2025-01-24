package io.polaris.core.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
@SuppressWarnings({"DataFlowIssue", "NullableProblems"})
public class WrappingThreadPoolExecutor extends ThreadPoolExecutor {
	private WrappingTaskFactory wrappedTaskFactory;

	public WrappingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public WrappingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}

	public WrappingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	public WrappingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	public WrappingTaskFactory getWrappedTaskFactory() {
		return wrappedTaskFactory;
	}

	public void setWrappedTaskFactory(WrappingTaskFactory wrappedTaskFactory) {
		this.wrappedTaskFactory = wrappedTaskFactory;
	}

	@Override
	public void execute(Runnable command) {
		if (command != null && !(command instanceof WrappingTask) && wrappedTaskFactory != null) {
			command = wrappedTaskFactory.wrap(command);
		}
		super.execute(command);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		if (runnable != null && !(runnable instanceof WrappingTask) && wrappedTaskFactory != null) {
			runnable = wrappedTaskFactory.wrap(runnable);
		}
		return new FutureTask<T>(runnable, value);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		if (callable != null && !(callable instanceof WrappingTask) && wrappedTaskFactory != null) {
			callable = wrappedTaskFactory.wrap(callable);
		}
		return new FutureTask<T>(callable);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		if (r instanceof WrappingExecutionListener) {
			((WrappingExecutionListener) r).beforeExecute(t, r);
		}
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		if (r instanceof WrappingExecutionListener) {
			((WrappingExecutionListener) r).afterExecute(r, t);
		}
	}
}

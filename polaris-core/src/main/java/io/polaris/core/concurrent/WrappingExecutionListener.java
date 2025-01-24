package io.polaris.core.concurrent;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public interface WrappingExecutionListener extends WrappingTask{

	void beforeExecute(Thread t, Runnable r);

	void afterExecute(Runnable r, Throwable t);
}

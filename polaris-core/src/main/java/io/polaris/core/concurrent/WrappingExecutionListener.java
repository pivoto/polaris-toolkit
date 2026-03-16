package io.polaris.core.concurrent;

import io.polaris.core.spi.Spi;
import io.polaris.core.spi.SpiLoadable;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
@Spi("线程任务执行监听器")
public interface WrappingExecutionListener extends WrappingTask, SpiLoadable {

	void beforeExecute(Thread t, Runnable r);

	void afterExecute(Runnable r, Throwable t);
}

package io.polaris.core.concurrent;

import javax.annotation.Nonnull;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public interface WrappingRunnable extends WrappingTask, Runnable {

	@Nonnull
	Runnable getOriginRunnable();

}

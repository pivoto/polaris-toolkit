package io.polaris.core.concurrent.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import io.polaris.core.spi.StatefulSpiLoadable;

/**
 * @author Qt
 * @since Aug 07, 2025
 */
public interface AsyncThreadPoolFactory extends StatefulSpiLoadable {

	ExecutorService buildExecutor();

	boolean canShutdownExecutor();

	ScheduledExecutorService buildScheduledExecutor();

	boolean canShutdownScheduledExecutor();

}

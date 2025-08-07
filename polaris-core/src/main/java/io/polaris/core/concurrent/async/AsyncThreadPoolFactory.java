package io.polaris.core.concurrent.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import io.polaris.core.service.StatefulServiceLoadable;

/**
 * @author Qt
 * @since Aug 07, 2025
 */
public interface AsyncThreadPoolFactory extends StatefulServiceLoadable {

	ExecutorService buildExecutor();

	boolean canShutdownExecutor();

	ScheduledExecutorService buildScheduledExecutor();

	boolean canShutdownScheduledExecutor();

}

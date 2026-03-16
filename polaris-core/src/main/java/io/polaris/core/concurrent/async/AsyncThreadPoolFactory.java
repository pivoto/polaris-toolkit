package io.polaris.core.concurrent.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import io.polaris.core.spi.Spi;
import io.polaris.core.spi.StatefulSpiLoadable;

/**
 * @author Qt
 * @since Aug 07, 2025
 */
@Spi("异步线程池构造工厂")
public interface AsyncThreadPoolFactory extends StatefulSpiLoadable {

	ExecutorService buildExecutor();

	boolean canShutdownExecutor();

	ScheduledExecutorService buildScheduledExecutor();

	boolean canShutdownScheduledExecutor();

}

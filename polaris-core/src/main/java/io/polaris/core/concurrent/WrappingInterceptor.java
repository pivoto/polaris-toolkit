package io.polaris.core.concurrent;

import io.polaris.core.spi.Spi;
import io.polaris.core.spi.SpiLoadable;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
@Spi("线程任务拦截器")
public interface WrappingInterceptor extends WrappingTask, SpiLoadable {

	void onBefore();

	void onAfter();

	void onThrowing(Throwable e);

	void onFinally();

}

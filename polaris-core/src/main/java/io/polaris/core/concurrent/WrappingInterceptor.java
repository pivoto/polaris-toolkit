package io.polaris.core.concurrent;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public interface WrappingInterceptor extends WrappingTask{

	void onBefore();

	void onAfter();

	void onThrowing(Throwable e);

	void onFinally();

}

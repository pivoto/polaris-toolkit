package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableRunnable {

	void run() throws Throwable;

}

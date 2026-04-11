package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExceptionalRunnable {

	void run() throws Exception;

}

package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableExecutable {

	void execute() throws Throwable;

}

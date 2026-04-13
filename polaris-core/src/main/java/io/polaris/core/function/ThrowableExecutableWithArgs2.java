package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableExecutableWithArgs2<A, B> {

	void execute(A a, B b) throws Throwable;

}

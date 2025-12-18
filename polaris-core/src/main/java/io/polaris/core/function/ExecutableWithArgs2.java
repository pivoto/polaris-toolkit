package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExecutableWithArgs2<A, B> {

	void execute(A a, B b) throws Exception;

}

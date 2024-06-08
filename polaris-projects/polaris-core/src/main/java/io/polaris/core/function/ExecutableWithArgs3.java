package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ExecutableWithArgs3<A, B, C> {

	void execute(A a, B b, C c) throws Exception;

}

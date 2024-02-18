package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ExecutableWithArg4<A, B, C, D> {

	void execute(A a, B b, C c, D d) throws Exception;

}

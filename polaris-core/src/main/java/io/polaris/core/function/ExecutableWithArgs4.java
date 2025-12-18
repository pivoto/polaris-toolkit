package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ExecutableWithArgs4<A, B, C, D> {

	void execute(A a, B b, C c, D d) throws Exception;

}

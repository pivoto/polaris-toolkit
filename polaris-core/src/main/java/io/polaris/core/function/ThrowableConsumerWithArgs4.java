package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableConsumerWithArgs4<A, B, C, D> {

	void accept(A a, B b, C c, D d) throws Throwable;

}

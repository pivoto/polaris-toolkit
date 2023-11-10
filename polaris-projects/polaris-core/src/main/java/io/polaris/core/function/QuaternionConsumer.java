package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface QuaternionConsumer<A, B, C, D> {

	void accept(A a, B b, C c, D d);
}

package io.polaris.core.function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface TernaryConsumer<A, B, C> {

	void accept(A a, B b, C c);
}

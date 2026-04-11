package io.polaris.core.function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableConsumerWithArgs7<A, B, C, D, E, F, G> {

	void accept(A a, B b, C c, D d, E e, F f, G g) throws Throwable;

}

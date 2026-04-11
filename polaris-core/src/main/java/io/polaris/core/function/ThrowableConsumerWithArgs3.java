package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ThrowableConsumerWithArgs3<A, B, C> {

	void accept(A a, B b, C c) throws Throwable;

}

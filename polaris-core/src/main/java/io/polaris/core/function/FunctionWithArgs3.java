package io.polaris.core.function;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface FunctionWithArgs3<A, B, C, R> {

	R apply(A a, B b, C c);

}

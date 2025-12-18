package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 */
@FunctionalInterface
public interface FunctionWithArgs<T, R> {

	R apply(T... args);

}

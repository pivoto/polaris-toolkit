package io.polaris.core.function;

import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface FunctionWithArgs<T, R> {

	R apply(T... args);

}

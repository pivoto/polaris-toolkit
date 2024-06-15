package io.polaris.core.function;

import java.util.function.BiFunction;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface FunctionWithArgs2<T, U, R> extends BiFunction<T, U, R> {

}

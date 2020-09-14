package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable, MethodReferenceReflection {
}

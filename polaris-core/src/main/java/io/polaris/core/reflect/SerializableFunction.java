package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable, MethodReferenceReflection {
}

package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableBiFunction<T, U, R> extends BiFunction<T, U, R>, Serializable, MethodReferenceReflection {

}

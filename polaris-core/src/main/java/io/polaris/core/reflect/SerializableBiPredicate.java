package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.BiPredicate;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableBiPredicate<T,U> extends BiPredicate<T,U>, Serializable, MethodReferenceReflection {
}

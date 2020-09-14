package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableBiPredicate<T,U> extends BiPredicate<T,U>, Serializable, MethodReferenceReflection {
}

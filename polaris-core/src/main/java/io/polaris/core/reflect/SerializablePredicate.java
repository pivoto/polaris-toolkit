package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializablePredicate<T> extends Predicate<T>, Serializable, MethodReferenceReflection {
}

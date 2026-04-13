package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializablePredicate<T> extends Predicate<T>, Serializable, MethodReferenceReflection {
}

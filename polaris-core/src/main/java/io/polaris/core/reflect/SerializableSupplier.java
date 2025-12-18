package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableSupplier<T> extends Supplier<T>, Serializable, MethodReferenceReflection {
}

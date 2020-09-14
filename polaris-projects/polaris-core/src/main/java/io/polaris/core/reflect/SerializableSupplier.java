package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableSupplier<T> extends Supplier<T>, Serializable, MethodReferenceReflection {
}

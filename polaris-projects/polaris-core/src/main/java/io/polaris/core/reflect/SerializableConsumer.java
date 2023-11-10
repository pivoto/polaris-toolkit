package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableConsumer<T> extends Consumer<T>, Serializable, MethodReferenceReflection {
}

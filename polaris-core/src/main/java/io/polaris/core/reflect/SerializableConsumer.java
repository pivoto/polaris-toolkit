package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableConsumer<T> extends Consumer<T>, Serializable, MethodReferenceReflection {
}

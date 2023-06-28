package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableRunnable extends Runnable, Serializable, MethodReferenceReflection {
}

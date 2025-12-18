package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableRunnable extends Runnable, Serializable, MethodReferenceReflection {
}

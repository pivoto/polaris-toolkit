package io.polaris.core.reflect;

import io.polaris.core.function.Executable;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableCallable<V> extends Callable<V>, Serializable, MethodReferenceReflection {
}

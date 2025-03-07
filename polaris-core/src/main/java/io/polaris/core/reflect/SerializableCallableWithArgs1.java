package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.concurrent.Callable;

import io.polaris.core.function.CallableWithArgs1;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableCallableWithArgs1<V, T> extends CallableWithArgs1<V, T>, Serializable, MethodReferenceReflection {
}

package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.CallableWithArgs1;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableCallableWithArgs1<T, V> extends CallableWithArgs1<T, V>, Serializable, MethodReferenceReflection {
}

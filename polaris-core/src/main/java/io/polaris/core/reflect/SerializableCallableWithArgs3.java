package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.CallableWithArgs3;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableCallableWithArgs3<A, B, C, V> extends CallableWithArgs3<A, B, C, V>, Serializable, MethodReferenceReflection {
}

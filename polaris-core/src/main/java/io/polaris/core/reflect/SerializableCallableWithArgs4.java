package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.CallableWithArgs4;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableCallableWithArgs4<A, B, C, D, V> extends CallableWithArgs4<A, B, C, D, V>, Serializable, MethodReferenceReflection {
}

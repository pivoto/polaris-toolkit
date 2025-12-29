package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.CallableWithArgs5;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableCallableWithArgs5<A, B, C, D, E, V> extends CallableWithArgs5<A, B, C, D, E, V>, Serializable, MethodReferenceReflection {
}

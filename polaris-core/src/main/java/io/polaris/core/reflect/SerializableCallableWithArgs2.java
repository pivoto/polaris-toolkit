package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.CallableWithArgs2;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableCallableWithArgs2<A, B, V> extends CallableWithArgs2<A, B, V>, Serializable, MethodReferenceReflection {
}

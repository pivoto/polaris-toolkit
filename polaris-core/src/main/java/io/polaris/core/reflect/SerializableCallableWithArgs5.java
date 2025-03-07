package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.CallableWithArgs4;
import io.polaris.core.function.CallableWithArgs5;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableCallableWithArgs5<V, A, B, C, D, E> extends CallableWithArgs5<V, A, B, C, D, E>, Serializable, MethodReferenceReflection {
}

package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.CallableWithArgs3;
import io.polaris.core.function.CallableWithArgs4;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableCallableWithArgs4<V, A, B, C, D> extends CallableWithArgs4<V, A, B, C, D>, Serializable, MethodReferenceReflection {
}

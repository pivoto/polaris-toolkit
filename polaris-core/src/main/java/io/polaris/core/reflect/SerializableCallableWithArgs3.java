package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.CallableWithArgs2;
import io.polaris.core.function.CallableWithArgs3;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableCallableWithArgs3<V, A, B, C> extends CallableWithArgs3<V, A, B, C>, Serializable, MethodReferenceReflection {
}

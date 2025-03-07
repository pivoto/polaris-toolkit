package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.CallableWithArgs1;
import io.polaris.core.function.CallableWithArgs2;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableCallableWithArgs2<V, A, B> extends CallableWithArgs2<V, A, B>, Serializable, MethodReferenceReflection {
}

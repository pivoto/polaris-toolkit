package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.CallableWithArgs1;
import io.polaris.core.function.CallableWithArgs2;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableCallableWithArgs2<V, A, B> extends CallableWithArgs2<V, A, B>, Serializable, MethodReferenceReflection {
}

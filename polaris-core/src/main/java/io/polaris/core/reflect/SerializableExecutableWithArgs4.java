package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.ExecutableWithArgs4;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableExecutableWithArgs4<A, B, C, D> extends ExecutableWithArgs4<A, B, C, D>, Serializable, MethodReferenceReflection {
}

package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.ExecutableWithArgs3;
import io.polaris.core.function.ExecutableWithArgs4;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableExecutableWithArgs4<A, B, C, D> extends ExecutableWithArgs4<A, B, C, D>, Serializable, MethodReferenceReflection {
}

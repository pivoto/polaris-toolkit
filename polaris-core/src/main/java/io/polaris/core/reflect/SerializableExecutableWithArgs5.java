package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.ExecutableWithArgs4;
import io.polaris.core.function.ExecutableWithArgs5;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableExecutableWithArgs5<A, B, C, D, E> extends ExecutableWithArgs5<A, B, C, D, E>, Serializable, MethodReferenceReflection {
}

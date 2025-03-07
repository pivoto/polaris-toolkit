package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.ExecutableWithArgs2;
import io.polaris.core.function.ExecutableWithArgs3;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableExecutableWithArgs3<A, B, C> extends ExecutableWithArgs3<A, B, C>, Serializable, MethodReferenceReflection {
}

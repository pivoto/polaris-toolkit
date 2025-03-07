package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.ExecutableWithArgs1;
import io.polaris.core.function.ExecutableWithArgs2;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableExecutableWithArgs2<A, B> extends ExecutableWithArgs2<A, B>, Serializable, MethodReferenceReflection {
}

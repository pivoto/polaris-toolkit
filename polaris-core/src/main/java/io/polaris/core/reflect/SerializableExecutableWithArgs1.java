package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.Executable;
import io.polaris.core.function.ExecutableWithArgs1;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableExecutableWithArgs1<T> extends ExecutableWithArgs1<T>, Serializable, MethodReferenceReflection {
}

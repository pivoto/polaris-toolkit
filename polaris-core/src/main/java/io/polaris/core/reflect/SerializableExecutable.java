package io.polaris.core.reflect;

import io.polaris.core.function.Executable;

import java.io.Serializable;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableExecutable extends Executable, Serializable, MethodReferenceReflection {
}

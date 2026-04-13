package io.polaris.core.reflect;

import java.io.Serializable;

import io.polaris.core.function.Executable;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableExecutable extends Executable, Serializable, MethodReferenceReflection {
}

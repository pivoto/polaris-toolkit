package io.polaris.core.reflect;

import io.polaris.core.function.Executable;

import java.io.Serializable;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableExecutable extends Executable, Serializable, MethodReferenceReflection {
}

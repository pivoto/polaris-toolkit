package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableTriFunction<A, B, C, R> extends Serializable, MethodReferenceReflection {

	R apply(A a, B b, C c);
}

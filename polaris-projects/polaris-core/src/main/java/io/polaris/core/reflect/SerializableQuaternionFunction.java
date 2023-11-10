package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableQuaternionFunction<A, B, C, D, R> extends Serializable, MethodReferenceReflection {

	R apply(A a, B b, C c, D d);
}

package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableFunctionWithArgs4<A, B, C, D, R> extends Serializable, MethodReferenceReflection {

	R apply(A a, B b, C c, D d);
}

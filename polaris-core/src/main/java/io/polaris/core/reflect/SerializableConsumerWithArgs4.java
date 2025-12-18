package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableConsumerWithArgs4<A, B, C, D> extends Serializable, MethodReferenceReflection {

	void accept(A a, B b, C c, D d);
}

package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableConsumerWithArgs4<A, B, C, D> extends Serializable, MethodReferenceReflection {

	void accept(A a, B b, C c, D d);
}

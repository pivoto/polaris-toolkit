package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableConsumerWithArgs3<A, B, C> extends Serializable, MethodReferenceReflection {

	void accept(A a, B b, C c);
}

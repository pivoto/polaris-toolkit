package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableConsumerWithArgs6<A, B, C, D, E, F> extends Serializable, MethodReferenceReflection {

	void accept(A a, B b, C c, D d, E e, F f);
}

package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableTriConsumer<A, B, C> extends Serializable, MethodReferenceReflection {

	void accept(A a, B b, C c);
}

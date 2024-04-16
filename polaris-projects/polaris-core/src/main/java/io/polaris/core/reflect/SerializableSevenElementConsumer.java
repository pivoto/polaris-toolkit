package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableSevenElementConsumer<A, B, C, D, E, F, G> extends Serializable, MethodReferenceReflection {

	void accept(A a, B b, C c, D d, E e, F f, G g);
}

package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface SerializableFunctionWithArgs7<A, B, C, D, E, F, G, R> extends Serializable, MethodReferenceReflection {

	R apply(A a, B b, C c, D d, E e, F f, G g);
}

package io.polaris.core.reflect;

import java.io.Serializable;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SerializableFunctionWithArgs7<A, B, C, D, E, F, G, R> extends Serializable, MethodReferenceReflection {

	R apply(A a, B b, C c, D d, E e, F f, G g);
}

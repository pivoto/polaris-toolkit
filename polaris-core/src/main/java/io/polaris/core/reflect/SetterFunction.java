package io.polaris.core.reflect;

import java.io.Serializable;
import java.util.function.BiConsumer;

/**
 * @author Qt
 */
@FunctionalInterface
public interface SetterFunction<T, U> extends BiConsumer<T, U>, Serializable, MethodReferenceReflection {

}

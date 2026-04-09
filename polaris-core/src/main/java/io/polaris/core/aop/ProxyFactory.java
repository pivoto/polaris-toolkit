package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import io.polaris.core.asm.internal.Signature;

/**
 * @author Qt
 * @since May 14, 2024
 */
public interface ProxyFactory {

	ProxyFactory classloader(ClassLoader classloader);

	ProxyFactory superclass(Class<?> superclass);

	ProxyFactory interfaces(Class<?>[] interfaces);

	ProxyFactory addAdvice(Advice... advices);

	ProxyFactory addAdvice(Predicate<Signature> predicate, Advice... advices);

	ProxyFactory addAdvice(BiPredicate<Signature, Method> predicate, Advice... advices);

	ProxyFactory override(Function<Signature, Function<Object[], Object>> overrideGenerator);

	ProxyFactory override(Signature method, Function<Object[], Object> overrideFunction);

	ProxyFactory override(Map<Signature, Function<Object[], Object>> overrideMapper);

	ProxyFactory defaults(Function<Signature, Function<Object[], Object>> defaultGenerator);

	ProxyFactory defaults(Signature method, Function<Object[], Object> defaultFunction);

	ProxyFactory defaults(Map<Signature, Function<Object[], Object>> defaultMapper);

	<T> T get();

}

package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since May 14, 2024
 */
public interface ProxyFactory {

	ProxyFactory classloader(ClassLoader classloader);

	ProxyFactory superclass(Class<?> superclass);

	ProxyFactory interfaces(Class<?>[] interfaces);

	ProxyFactory addAdvice(Advice... advices);

	ProxyFactory addAdvice(Predicate<Method> predicate, Advice... advices);

	ProxyFactory override(Function<Method, Function<Object[], Object>> overrideGenerator);

	ProxyFactory override(Method method, Function<Object[], Object> overrideFunction);

	ProxyFactory override(Map<Method, Function<Object[], Object>> overrideMapper);

	ProxyFactory defaults(Function<Method, Function<Object[], Object>> defaultGenerator);

	ProxyFactory defaults(Method method, Function<Object[], Object> defaultFunction);

	ProxyFactory defaults(Map<Method, Function<Object[], Object>> defaultMapper);

	<T> T get();

}

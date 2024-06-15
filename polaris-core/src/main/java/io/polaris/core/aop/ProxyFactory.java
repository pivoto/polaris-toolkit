package io.polaris.core.aop;

import java.lang.reflect.Method;
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

	<T> T get();

}

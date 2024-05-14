package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class JdkProxyFactory implements ProxyFactory {

	private final Object target;
	private final JdkAspect aspect;
	private Class<?> superclass;
	private Class<?>[] interfaces;
	private ClassLoader classloader;

	public JdkProxyFactory(Object target) {
		this.target = target;
		this.aspect = new JdkAspect(target);
		this.classloader = Thread.currentThread().getContextClassLoader();
	}

	@Override
	public JdkProxyFactory classloader(ClassLoader classloader) {
		this.classloader = classloader;
		return this;
	}

	@Override
	public JdkProxyFactory superclass(Class<?> superclass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public JdkProxyFactory interfaces(Class<?>[] interfaces) {
		this.interfaces = interfaces;
		return this;
	}

	@Override
	public JdkProxyFactory addAdvice(Advice... advices) {
		aspect.addAdvice(advices);
		return this;
	}

	@Override
	public JdkProxyFactory addAdvice(Predicate<Method> predicate, Advice... advices) {
		aspect.addAdvice(predicate, advices);
		return this;
	}

	@Override
	public <T> T get() {
		return ProxyUtils.jdkProxy(classloader, interfaces, aspect);
	}

}

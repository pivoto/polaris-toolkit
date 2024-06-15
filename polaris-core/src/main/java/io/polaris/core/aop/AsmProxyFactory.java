package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import io.polaris.core.assertion.Assertions;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class AsmProxyFactory implements ProxyFactory {

	private final Object target;
	private final Aspect aspect;
	private Class<?> superclass;
	private Class<?>[] interfaces;
	private ClassLoader classloader;

	public AsmProxyFactory(Object target) {
		this.target = target;
		this.aspect = new Aspect(target);
		this.classloader = Thread.currentThread().getContextClassLoader();
	}

	@Override
	public AsmProxyFactory classloader(ClassLoader classloader) {
		this.classloader = classloader;
		return this;
	}

	@Override
	public AsmProxyFactory superclass(Class<?> superclass) {
		if (superclass != null) {
			Assertions.assertInstanceOf(superclass, target, "target type is not matched superclass");
		} else {
			superclass = target.getClass();
		}
		this.superclass = superclass;
		return this;
	}

	@Override
	public AsmProxyFactory interfaces(Class<?>[] interfaces) {
		this.interfaces = interfaces;
		return this;
	}

	@Override
	public AsmProxyFactory addAdvice(Advice... advices) {
		aspect.addAdvice(advices);
		return this;
	}

	@Override
	public AsmProxyFactory addAdvice(Predicate<Method> predicate, Advice... advices) {
		aspect.addAdvice(predicate, advices);
		return this;
	}

	@Override
	public <T> T get() {
		return ProxyUtils.proxy(classloader, superclass == null ? target.getClass() : superclass, interfaces, aspect);
	}

}

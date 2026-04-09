package io.polaris.core.aop;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import io.polaris.core.asm.internal.Signature;
import io.polaris.core.asm.proxy.Invocation;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.tuple.Tuple2;
import io.polaris.core.tuple.Tuples;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class JdkProxyFactory implements ProxyFactory {
	private static final Logger log = Loggers.of(JdkProxyFactory.class);

	private final Object target;
	private final Aspect aspect;
	private Class<?> superclass;
	private Class<?>[] interfaces;
	private ClassLoader classloader;

	public JdkProxyFactory(Object target) {
		this.target = target;
		this.aspect = new Aspect(target);
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
	public JdkProxyFactory addAdvice(Predicate<Signature> predicate, Advice... advices) {
		aspect.addAdvice(predicate, advices);
		return this;
	}

	@Override
	public JdkProxyFactory addAdvice(BiPredicate<Signature, Method> predicate, Advice... advices) {
		aspect.addAdvice(predicate, advices);
		return this;
	}

	@Override
	public ProxyFactory override(Function<Signature, Function<Object[], Object>> overrideGenerator) {
		aspect.override(overrideGenerator);
		return this;
	}

	@Override
	public ProxyFactory override(Signature method, Function<Object[], Object> overrideFunction) {
		aspect.override(method, overrideFunction);
		return this;
	}

	@Override
	public ProxyFactory override(Map<Signature, Function<Object[], Object>> overrideMapper) {
		aspect.override(overrideMapper);
		return this;
	}

	@Override
	public ProxyFactory defaults(Function<Signature, Function<Object[], Object>> defaultGenerator) {
		aspect.defaults(defaultGenerator);
		return this;
	}

	@Override
	public ProxyFactory defaults(Signature method, Function<Object[], Object> defaultFunction) {
		aspect.defaults(method, defaultFunction);
		return this;
	}

	@Override
	public ProxyFactory defaults(Map<Signature, Function<Object[], Object>> defaultMapper) {
		aspect.defaults(defaultMapper);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get() {
		ClassLoader classloader = this.classloader;
		if (classloader == null) {
			classloader = Thread.currentThread().getContextClassLoader();
		}
		JdkInvocationHandler handler = new JdkInvocationHandler(target, aspect, interfaces);
		Class<?>[] interfaces = handler.getInterfaces();
		return (T) Proxy.newProxyInstance(classloader, interfaces, handler);
	}


}

package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import io.polaris.core.asm.proxy.Enhancer;
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
	/** 重写方法生成器 */
	private Function<Method, Function<Object[], Object>> overrideGenerator;
	/** 重写方法映射表 */
	private Map<Method, Function<Object[], Object>> overrideMapper;
	/** 默认方法生成器 */
	protected Function<Method, Function<Object[], Object>> defaultGenerator;
	/** 默认方法映射表 */
	protected Map<Method, Function<Object[], Object>> defaultMapper;
	/** 是否已生成代理类，如已生成过，则重写方法不支持在字节码层面注入，只能在切面中动态处理 */
	private volatile boolean generated = false;

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
		if (!superclass.equals(this.superclass)) {
			generated = false;
		}
		this.superclass = superclass;
		return this;
	}

	@Override
	public AsmProxyFactory interfaces(Class<?>[] interfaces) {
		if (!Arrays.deepEquals(interfaces, this.interfaces)) {
			generated = false;
		}
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
	public AsmProxyFactory override(Function<Method, Function<Object[], Object>> overrideGenerator) {
		if (generated) {
			aspect.override(overrideGenerator);
		} else {
			this.overrideGenerator = overrideGenerator;
		}
		return this;
	}

	@Override
	public AsmProxyFactory override(Method method, Function<Object[], Object> overrideFunction) {
		if (generated) {
			aspect.override(method, overrideFunction);
		} else {
			if (this.overrideMapper == null) {
				this.overrideMapper = new ConcurrentHashMap<>();
			}
			this.overrideMapper.put(method, overrideFunction);
		}
		return this;
	}

	@Override
	public AsmProxyFactory override(Map<Method, Function<Object[], Object>> overrideMapper) {
		if (generated) {
			aspect.override(overrideMapper);
		} else {
			if (overrideMapper == null || overrideMapper.isEmpty()) {
				this.overrideMapper = null;
			}
			this.overrideMapper = new ConcurrentHashMap<>(overrideMapper);
		}
		return this;
	}

	@Override
	public AsmProxyFactory defaults(Function<Method, Function<Object[], Object>> defaultGenerator) {
		if (generated) {
			aspect.defaults(defaultGenerator);
		} else {
			this.defaultGenerator = defaultGenerator;
		}
		return this;
	}

	@Override
	public AsmProxyFactory defaults(Method method, Function<Object[], Object> defaultFunction) {
		if (generated) {
			aspect.defaults(method, defaultFunction);
		} else {
			if (this.defaultMapper == null) {
				this.defaultMapper = new ConcurrentHashMap<>();
			}
			this.defaultMapper.put(method, defaultFunction);
		}
		return this;
	}

	@Override
	public AsmProxyFactory defaults(Map<Method, Function<Object[], Object>> defaultMapper) {
		if (generated) {
			aspect.defaults(defaultMapper);
		} else {
			if (defaultMapper == null || defaultMapper.isEmpty()) {
				this.defaultMapper = null;
			}
			this.defaultMapper = new ConcurrentHashMap<>(defaultMapper);
		}
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get() {
		if (classloader == null) {
			classloader = Thread.currentThread().getContextClassLoader();
		}
		Enhancer enhancer = new Enhancer();
		enhancer.setClassLoader(classloader);
		T proxy = (T) enhancer.superclass(superclass == null ? target.getClass() : superclass)
			.interfaces(appendInterfaces(interfaces))
			.interceptor(aspect)
			.override(overrideGenerator)
			.override(overrideMapper)
			.defaults(defaultGenerator)
			.defaults(defaultMapper)
			.create();
		generated = true;
		return proxy;
	}


	private static Class<?>[] appendInterfaces(Class<?>[] interfaces) {
		if (interfaces == null || interfaces.length == 0) {
			return new Class[]{AopProxy.class};
		}
		Class<?>[] array = new Class[interfaces.length + 1];
		System.arraycopy(interfaces, 0, array, 0, interfaces.length);
		array[interfaces.length] = AopProxy.class;
		return array;
	}
}

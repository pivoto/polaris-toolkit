package io.polaris.mybatis.enhance.printer;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.util.MapUtil;

/**
 * @author Qt
 * @since Apr 03, 2026
 */
class RecorderMapperProxy<T> implements InvocationHandler, Serializable {
	private static final long serialVersionUID = -2294693632812972833L;
	private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
		| MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;
	private static final Constructor<MethodHandles.Lookup> lookupConstructor;
	private static final Method privateLookupInMethod;

	static {
		Method privateLookupIn;
		try {
			privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
		} catch (NoSuchMethodException e) {
			privateLookupIn = null;
		}
		privateLookupInMethod = privateLookupIn;

		Constructor<MethodHandles.Lookup> lookup = null;
		if (privateLookupInMethod == null) {
			// JDK 1.8
			try {
				lookup = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
				lookup.setAccessible(true);
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException(
					"There is neither 'privateLookupIn(Class, Lookup)' nor 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.",
					e);
			} catch (Exception e) {
				lookup = null;
			}
		}
		lookupConstructor = lookup;
	}

	private final Class<T> mapperInterface;
	private final Map<Method, RecorderMapperMethodInvoker> methodCache;
	private final Configuration configuration;
	private final Consumer<BoundSql> consumer;

	public RecorderMapperProxy(Class<T> mapperInterface, Map<Method, RecorderMapperMethodInvoker> methodCache, Configuration configuration, Consumer<BoundSql> consumer) {
		this.mapperInterface = mapperInterface;
		this.methodCache = methodCache;
		this.configuration = configuration;
		this.consumer = consumer;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			if (Object.class.equals(method.getDeclaringClass())) {
				return method.invoke(this, args);
			} else {
				return cachedInvoker(method).invoke(proxy, method, args);
			}
		} catch (Throwable t) {
			throw ExceptionUtil.unwrapThrowable(t);
		}
	}

	private RecorderMapperMethodInvoker cachedInvoker(Method method) throws Throwable {
		try {
			return MapUtil.computeIfAbsent(methodCache, method, m -> {
				if (m.isDefault()) {
					try {
						if (privateLookupInMethod == null) {
							return new DefaultRecorderMethodInvoker(getMethodHandleJava8(method));
						} else {
							return new DefaultRecorderMethodInvoker(getMethodHandleJava9(method));
						}
					} catch (IllegalAccessException | InstantiationException | InvocationTargetException
									 | NoSuchMethodException e) {
						throw new RuntimeException(e);
					}
				} else {
					String statementId = mapperInterface.getName() + "." + method.getName();
					MappedStatement mappedStatement = configuration.getMappedStatement(statementId, false);
					SqlSource sqlSource = mappedStatement.getSqlSource();
					ParamNameResolver paramNameResolver = new ParamNameResolver(configuration, method);
					return new PlainRecorderMethodInvoker(sqlSource, paramNameResolver, consumer);
				}
			});
		} catch (RuntimeException re) {
			Throwable cause = re.getCause();
			throw cause == null ? re : cause;
		}
	}

	private MethodHandle getMethodHandleJava9(Method method)
		throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final Class<?> declaringClass = method.getDeclaringClass();
		return ((MethodHandles.Lookup) privateLookupInMethod.invoke(null, declaringClass, MethodHandles.lookup())).findSpecial(
			declaringClass, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
			declaringClass);
	}

	private MethodHandle getMethodHandleJava8(Method method)
		throws IllegalAccessException, InstantiationException, InvocationTargetException {
		final Class<?> declaringClass = method.getDeclaringClass();
		return lookupConstructor.newInstance(declaringClass, ALLOWED_MODES).unreflectSpecial(method, declaringClass);
	}
}

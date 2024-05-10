package io.polaris.core.jdbc.executor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.Jdbcs;
import io.polaris.core.jdbc.base.JdbcOptions;
import io.polaris.core.jdbc.base.ResultExtractor;
import io.polaris.core.jdbc.base.ResultRowMapper;
import io.polaris.core.jdbc.base.ResultSetVisitor;
import io.polaris.core.jdbc.base.ResultSetVisitors;
import io.polaris.core.jdbc.base.ResultVisitor;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.lang.JavaType;
import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since  Feb 06, 2024
 */
public class JdbcExecutor<T> implements InvocationHandler, InvocationHandlerHolder {
	private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
		| MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;
	private static final Constructor<MethodHandles.Lookup> lookupConstructor;
	private static final Method privateLookupInMethod;
	private final Class<T> interfaceClass;
	private final Map<Method, Function<Object[], Object>> methodFunctions;

	static {
		// @see org.apache.ibatis.binding.MapperProxy
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

	protected JdbcExecutor(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
		Map<Method, Function<Object[], Object>> methodFunctions = new HashMap<>();
		JdbcExecutorMetadata<T> metadata = JdbcExecutorMetadata.of(interfaceClass);
		metadata.getMethodMetadataMap().forEach((method, methodMetadata) ->
			methodFunctions.put(method, buildMethodFunction(methodMetadata)));
		this.methodFunctions = Collections.unmodifiableMap(methodFunctions);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == InvocationHandlerHolder.class) {
			return Reflects.invoke(this, method, args);
		}
		if (Reflects.isToStringMethod(method)) {
			return interfaceClass.getName() + "@" + Integer.toHexString(this.hashCode());
		}
		if (Reflects.isEqualsMethod(method)) {
			Object arg = args[0];
			if (Proxy.isProxyClass(arg.getClass()) && arg instanceof InvocationHandlerHolder && interfaceClass.isInstance(arg)) {
				return ((InvocationHandlerHolder) arg).$handler().equals(this);
			}
			return false;
		}
		if (Reflects.isHashCodeMethod(method)) {
			return this.hashCode();
		}

		if (method.isDefault()) {
			try {
				MethodHandle methodHandle;
				if (privateLookupInMethod == null) {
					methodHandle = getMethodHandleJava8(method);
				} else {
					methodHandle = getMethodHandleJava9(method);
				}
				return methodHandle.bindTo(proxy).invokeWithArguments(args);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}

		Function<Object[], Object> function = methodFunctions.get(method);
		if (function == null) {
			throw new IllegalStateException("Unexpected method: " + method);
		}
		return function.apply(args);
	}

	@Override
	public InvocationHandler $handler() {
		return this;
	}

	protected Function<Object[], Object> buildMethodFunction(MethodMetadata meta) {
		if (meta.isSelect()) {
			return buildSelectFunction(meta);
		}
		return buildUpdateFunction(meta);
	}

	protected Function<Object[], Object> buildUpdateFunction(MethodMetadata meta) {
		return args -> {
			try {
				MethodArgs methodArgs = meta.getArgsBuilder().apply(args);
				Map<String, Object> bindings = methodArgs.getBindings();
				SqlNode sqlNode = meta.getSqlBuilder().apply(bindings);
				JdbcOptions options = methodArgs.getOptions();
				Object noKeyArg = methodArgs.getNoKeyArg();
				Connection conn = getConnection(methodArgs);
				int rows = Jdbcs.update(conn, sqlNode, options, noKeyArg != null ? noKeyArg : bindings);
				JavaType<?> returnType = meta.getReturnType();
				if (void.class.equals(returnType.getRawClass())) {
					return null;
				}
				return Converters.convertQuietly(returnType, rows);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		};
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected Function<Object[], Object> buildSelectFunction(MethodMetadata meta) {
		return (args) -> {
			try {
				MethodArgs methodArgs = meta.getArgsBuilder().apply(args);
				Map<String, Object> bindings = methodArgs.getBindings();
				SqlNode sqlNode = meta.getSqlBuilder().apply(bindings);
				JdbcOptions options = methodArgs.getOptions();
				Connection conn = getConnection(methodArgs);

				ResultExtractor<?> extractor = methodArgs.getExtractor();
				if (extractor == null) {
					extractor = meta.getExtractor();
				}

				if (extractor == null) {
					ResultVisitor visitor = methodArgs.getVisitor();
					ResultSetVisitor resultSetVisitor = null;
					if (visitor != null) {
						ResultRowMapper visitorRowMapper = methodArgs.getVisitorRowMapper();
						resultSetVisitor = ResultSetVisitors.ofRows(visitorRowMapper, visitor);
					}
					Jdbcs.query(conn, sqlNode, options, resultSetVisitor);
					return null;
				}
				return Jdbcs.query(conn, sqlNode, options, extractor);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		};
	}

	protected Connection getConnection(MethodArgs methodArgs) {
		Connection conn = methodArgs.getConnection();
		if (conn == null) {
			conn = JdbcExecutors.getCurrentConnection();
		}
		if (conn == null) {
			throw new IllegalStateException("缺少数据库连接对象");
		}
		return conn;
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

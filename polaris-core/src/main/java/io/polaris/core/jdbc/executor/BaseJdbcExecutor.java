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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import io.polaris.core.converter.Converters;
import io.polaris.core.err.InvocationException;
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
 * @since May 06, 2026
 */
public abstract class BaseJdbcExecutor<T> implements InvocationHandler, InvocationHandlerHolder {
	private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
		| MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;
	private static final Constructor<MethodHandles.Lookup> lookupConstructor;
	private static final Method privateLookupInMethod;
	private Map<Method, MethodHandle> methodHandleCache;
	protected final Class<T> interfaceClass;

	static {
		// @see org.apache.ibatis.binding.MapperProxy
		Method privateLookupIn;
		try {
			//noinspection JavaReflectionMemberAccess
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

	protected BaseJdbcExecutor(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	@Override
	public InvocationHandler $handler() {
		return this;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == InvocationHandlerHolder.class) {
			if (method.getName().equals("$handler") && args.length == 0) {
				return this.$handler();
			}
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
				MethodHandle methodHandle = getCachedMethodHandle(method);
				return methodHandle.bindTo(proxy).invokeWithArguments(args);
			} catch (ReflectiveOperationException e) {
				throw new InvocationException(e);
			}
		}
		return doInvoke(proxy, method, args);
	}

	protected abstract Object doInvoke(Object proxy, Method method, Object[] args) throws Throwable;


	protected Object doBatchUpdate(MethodMetadata meta, Function<MethodArgs, Connection> connGetter, JdbcBatch batch, Object[] args) {
		try {
			MethodArgs methodArgs = meta.getArgsBuilder().apply(args);
			Map<String, Object> bindings = methodArgs.getBindings();
			SqlNode sqlNode = meta.getSqlBuilder().apply(bindings);
			JdbcOptions options = methodArgs.getOptions();
			Object noKeyArg = methodArgs.getNoKeyArg();
			Connection conn = connGetter.apply(methodArgs);
			Object generatedKeyBinding = noKeyArg != null ? noKeyArg : bindings;
			int rows = doBatchUpdate(batch, conn, sqlNode, options, generatedKeyBinding);
			JavaType<?> returnType = meta.getReturnType();
			if (void.class.equals(returnType.getRawClass())) {
				return null;
			}
			return Converters.convertQuietly(returnType, rows);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected Object doUpdate(MethodMetadata meta, Function<MethodArgs, Connection> connGetter, Object[] args) {
		try {
			MethodArgs methodArgs = meta.getArgsBuilder().apply(args);
			Map<String, Object> bindings = methodArgs.getBindings();
			SqlNode sqlNode = meta.getSqlBuilder().apply(bindings);
			JdbcOptions options = methodArgs.getOptions();
			Object noKeyArg = methodArgs.getNoKeyArg();
			Connection conn = connGetter.apply(methodArgs);
			Object generatedKeyBinding = noKeyArg != null ? noKeyArg : bindings;
			int rows = doUpdate(conn, sqlNode, options, generatedKeyBinding);
			JavaType<?> returnType = meta.getReturnType();
			if (void.class.equals(returnType.getRawClass())) {
				return null;
			}
			return Converters.convertQuietly(returnType, rows);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected Object doQuery(MethodMetadata meta, Function<MethodArgs, Connection> connGetter, Object[] args) {
		try {
			MethodArgs methodArgs = meta.getArgsBuilder().apply(args);
			Map<String, Object> bindings = methodArgs.getBindings();
			SqlNode sqlNode = meta.getSqlBuilder().apply(bindings);
			JdbcOptions options = methodArgs.getOptions();
			Connection conn = connGetter.apply(methodArgs);

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
				doQuery(conn, sqlNode, options, resultSetVisitor);
				return null;
			}
			return doQuery(conn, sqlNode, options, extractor);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected int doBatchUpdate(JdbcBatch batch, Connection conn, SqlNode sqlNode, JdbcOptions options, Object generatedKeyBinding) throws SQLException {
		if (conn == null) {
			throw new IllegalArgumentException("缺少数据库连接对象");
		}
		batch.update(conn, sqlNode, options, generatedKeyBinding);
		return -1;
	}

	protected int doUpdate(Connection conn, SqlNode sqlNode, JdbcOptions options, Object generatedKeyBinding) throws SQLException {
		if (conn == null) {
			throw new IllegalArgumentException("缺少数据库连接对象");
		}
		return Jdbcs.update(conn, sqlNode, options, generatedKeyBinding);
	}

	protected void doQuery(Connection conn, SqlNode sqlNode, JdbcOptions options, ResultSetVisitor resultSetVisitor) throws SQLException {
		if (conn == null) {
			throw new IllegalArgumentException("缺少数据库连接对象");
		}
		Jdbcs.query(conn, sqlNode, options, resultSetVisitor);
	}

	protected Object doQuery(Connection conn, SqlNode sqlNode, JdbcOptions options, ResultExtractor<?> extractor) throws SQLException {
		if (conn == null) {
			throw new IllegalArgumentException("缺少数据库连接对象");
		}
		return Jdbcs.query(conn, sqlNode, options, extractor);
	}


	protected MethodHandle getCachedMethodHandle(Method method) {
		Map<Method, MethodHandle> methodHandleCache = this.methodHandleCache;
		if (methodHandleCache == null) {
			methodHandleCache = new ConcurrentHashMap<>();
			this.methodHandleCache = methodHandleCache;
		}
		MethodHandle methodHandle = methodHandleCache.get(method);
		if (methodHandle == null) {
			try {
				if (privateLookupInMethod == null) {
					methodHandle = getMethodHandleJava8(method);
				} else {
					methodHandle = getMethodHandleJava9(method);
				}
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
			methodHandleCache.put(method, methodHandle);
		}
		return methodHandle;
	}

	protected MethodHandle getMethodHandleJava9(Method method)
		throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final Class<?> declaringClass = method.getDeclaringClass();
		return ((MethodHandles.Lookup) privateLookupInMethod.invoke(null, declaringClass, MethodHandles.lookup())).findSpecial(
			declaringClass, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
			declaringClass);
	}

	protected MethodHandle getMethodHandleJava8(Method method)
		throws IllegalAccessException, InstantiationException, InvocationTargetException {
		final Class<?> declaringClass = method.getDeclaringClass();
		return lookupConstructor.newInstance(declaringClass, ALLOWED_MODES).unreflectSpecial(method, declaringClass);
	}

}

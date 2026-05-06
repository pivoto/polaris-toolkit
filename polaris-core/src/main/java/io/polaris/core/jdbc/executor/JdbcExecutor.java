package io.polaris.core.jdbc.executor;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since Feb 06, 2024
 */
public class JdbcExecutor<T> extends BaseJdbcExecutor<T> {
	private final Map<Method, JdbcExecutorFunction> methodFunctions;

	protected JdbcExecutor(Class<T> interfaceClass) {
		super(interfaceClass);
		Map<Method, JdbcExecutorFunction> methodFunctions = new HashMap<>();
		JdbcExecutorMetadata<T> metadata = JdbcExecutorMetadata.of(interfaceClass);
		metadata.getMethodMetadataMap().forEach((method, methodMetadata) ->
			methodFunctions.put(method, buildMethodFunction(methodMetadata)));
		this.methodFunctions = Collections.unmodifiableMap(methodFunctions);
	}

	@Override
	public Object doInvoke(Object proxy, Method method, Object[] args) throws Throwable {
		JdbcExecutorFunction function = methodFunctions.get(method);
		if (function == null) {
			throw new IllegalStateException("Unexpected method: " + method);
		}
		return function.apply(this::getConnection, args);
	}

	protected Connection getConnection(MethodArgs methodArgs) {
		Connection conn = methodArgs.getConnection();
		if (conn == null) {
			conn = JdbcExecutors.getCurrentConnection();
		}
		return conn;
	}

	protected JdbcExecutorFunction buildMethodFunction(MethodMetadata meta) {
		if (meta.isSelect()) {
			return (connGetter, args) -> doQuery(meta, connGetter, args);
		}
		return (connGetter, args) -> doUpdate(meta, connGetter, args);
	}

}

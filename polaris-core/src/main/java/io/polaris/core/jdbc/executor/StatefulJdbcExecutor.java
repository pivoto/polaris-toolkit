package io.polaris.core.jdbc.executor;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since Feb 07, 2024
 */
public class StatefulJdbcExecutor<T> extends BaseJdbcExecutor<T> implements JdbcBatchExecutor {
	private final Map<Method, StatefulJdbcExecutorFunction> methodFunctions;
	private final Connection connection;
	private final JdbcBatch batch;

	protected StatefulJdbcExecutor(StatefulJdbcExecutor<T> template, Connection connection, boolean batch) {
		super(template.interfaceClass);
		this.connection = connection;
		if (batch) {
			this.batch = new JdbcBatch();
		} else {
			this.batch = null;
		}
		this.methodFunctions = template.methodFunctions;
	}

	protected StatefulJdbcExecutor(Class<T> interfaceClass, Connection connection, boolean batch) {
		super(interfaceClass);
		this.connection = connection;
		if (batch) {
			this.batch = new JdbcBatch();
		} else {
			this.batch = null;
		}
		Map<Method, StatefulJdbcExecutorFunction> methodFunctions = new HashMap<>();
		JdbcExecutorMetadata<T> metadata = JdbcExecutorMetadata.of(interfaceClass);
		metadata.getMethodMetadataMap().forEach((method, methodMetadata) ->
			methodFunctions.put(method, buildMethodFunction(methodMetadata)));
		this.methodFunctions = Collections.unmodifiableMap(methodFunctions);
	}

	@Override
	public Object doInvoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == JdbcBatchExecutor.class) {
			if (method.getName().equals("flush")) {
				return this.flush();
			}
			return Reflects.invoke(this, method, args);
		}
		StatefulJdbcExecutorFunction function = methodFunctions.get(method);
		if (function == null) {
			throw new IllegalStateException("Unexpected method: " + method);
		}
		return function.apply(this::getConnection, batch, args);
	}

	@Override
	public List<BatchResult> flush() throws SQLException {
		if (batch != null) {
			return batch.flush();
		}
		return null;
	}

	protected Connection getConnection(MethodArgs methodArgs) {
		Connection conn = methodArgs.getConnection();
		if (conn == null) {
			conn = this.connection;
		}
		if (conn == null) {
			conn = JdbcExecutors.getCurrentConnection();
		}
		return conn;
	}

	protected StatefulJdbcExecutorFunction buildMethodFunction(MethodMetadata meta) {
		if (meta.isSelect()) {
			return (connGetter, jdbcBatch, args) -> doQuery(meta, connGetter, args);
		}
		return (connGetter, jdbcBatch, args) -> {
			if (jdbcBatch != null) {
				return doBatchUpdate(meta, connGetter, jdbcBatch, args);
			} else {
				return doUpdate(meta, connGetter, args);
			}
		};
	}

}

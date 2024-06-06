package io.polaris.core.jdbc.executor;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.base.JdbcOptions;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.lang.JavaType;
import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since  Feb 07, 2024
 */
public class StatefulJdbcExecutor<T> extends JdbcExecutor<T> implements JdbcBatchExecutor {
	private final Connection connection;
	private final JdbcBatch batch;

	protected StatefulJdbcExecutor(Class<T> interfaceClass, Connection connection, boolean batch) {
		super(interfaceClass);
		this.connection = connection;
		if (batch) {
			this.batch = new JdbcBatch();
		} else {
			this.batch = null;
		}
	}

	@Override
	protected Connection getConnection(MethodArgs methodArgs) {
		Connection conn = methodArgs.getConnection();
		if (conn == null) {
			conn = this.connection;
		}
		if (conn == null) {
			conn = JdbcExecutors.getCurrentConnection();
		}
		if (conn != null) {
			return conn;
		}
		throw new IllegalArgumentException("缺少数据库连接对象");
	}

	@Override
	protected Function<Object[], Object> buildMethodFunction(MethodMetadata meta) {
		if (batch == null) {
			return super.buildMethodFunction(meta);
		}
		if (meta.isSelect()) {
			return buildSelectFunction(meta);
		}
		return args -> {
			try {
				MethodArgs methodArgs = meta.getArgsBuilder().apply(args);
				Map<String, Object> bindings = methodArgs.getBindings();
				SqlNode sqlNode = meta.getSqlBuilder().apply(bindings);
				JdbcOptions options = methodArgs.getOptions();
				Object noKeyArg = methodArgs.getNoKeyArg();
				Connection conn = getConnection(methodArgs);
				batch.update(conn, sqlNode, options, noKeyArg != null ? noKeyArg : bindings);

				JavaType<?> returnType = meta.getReturnType();
				if (void.class.equals(returnType.getRawClass())) {
					return null;
				}
				return Converters.convertQuietly(returnType, -1);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		};
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == JdbcBatchExecutor.class) {
			return Reflects.invoke(this, method, args);
		}
		return super.invoke(proxy, method, args);
	}

	@Override
	public List<BatchResult> flush() throws SQLException {
		if (batch != null) {
			return batch.flush();
		}
		return null;
	}

}

package io.polaris.core.jdbc.executor;

import java.sql.Connection;
import java.util.function.Function;

/**
 * @author Qt
 * @since May 06, 2026
 */
@FunctionalInterface
public interface JdbcExecutorFunction {

	Object apply(Function<MethodArgs, Connection> connGetter, Object[] args);

}

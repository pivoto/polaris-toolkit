package io.polaris.core.jdbc.executor;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Qt
 * @since  Feb 07, 2024
 */
public interface JdbcBatchExecutor {

	List<BatchResult> flush() throws SQLException;

}

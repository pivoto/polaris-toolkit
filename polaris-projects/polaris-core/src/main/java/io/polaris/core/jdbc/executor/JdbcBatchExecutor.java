package io.polaris.core.jdbc.executor;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Feb 07, 2024
 */
public interface JdbcBatchExecutor {

	List<BatchResult> flush() throws SQLException;

}

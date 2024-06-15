package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since  Feb 06, 2024
 */
@FunctionalInterface
public interface GeneratedKeyConsumer {

	void accept(ResultSet rs) throws SQLException;
}

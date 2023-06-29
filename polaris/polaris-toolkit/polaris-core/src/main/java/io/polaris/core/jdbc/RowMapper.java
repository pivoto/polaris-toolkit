package io.polaris.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 */
public interface RowMapper<T> {

	/**
	 * row to object
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	T map(ResultSet rs) throws SQLException;
}

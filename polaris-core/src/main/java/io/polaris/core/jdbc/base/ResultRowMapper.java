package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ResultRowMapper<T> {

	/**
	 * row to object
	 *
	 * @param rs
	 * @param columns
	 * @return
	 * @throws SQLException
	 */
	T map(ResultSet rs, String[] columns) throws SQLException;

}

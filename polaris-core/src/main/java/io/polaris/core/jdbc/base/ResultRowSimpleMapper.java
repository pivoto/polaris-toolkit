package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 */
@FunctionalInterface
public interface ResultRowSimpleMapper<T> {

	/**
	 * row to object
	 *
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	T map(ResultSet rs) throws SQLException;


}

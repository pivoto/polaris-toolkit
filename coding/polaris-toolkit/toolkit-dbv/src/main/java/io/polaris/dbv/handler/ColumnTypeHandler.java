package io.polaris.dbv.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 */
public interface ColumnTypeHandler<T> {

	/**
	 * get col value from rs
	 * @param rs
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	T getColumnValue(final ResultSet rs, final String columnName) throws SQLException;
}

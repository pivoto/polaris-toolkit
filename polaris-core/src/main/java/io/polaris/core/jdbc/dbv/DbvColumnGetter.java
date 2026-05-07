package io.polaris.core.jdbc.dbv;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 */
public interface DbvColumnGetter {

	/**
	 * get col value from rs
	 *
	 * @param rs
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	Object getColumnValue(final ResultSet rs, final String columnName) throws SQLException;
}

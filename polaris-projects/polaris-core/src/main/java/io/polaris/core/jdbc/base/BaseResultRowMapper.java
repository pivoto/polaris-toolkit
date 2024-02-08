package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
public abstract class BaseResultRowMapper<T> implements ResultRowMapper<T> {
	private ResultSet rsLast;
	private String[] colsLast;

	@Override
	public T map(ResultSet rs, String[] columns) throws SQLException {
		if (columns == null || columns.length == 0) {
			if (rsLast == rs) {
				columns = colsLast;
			} else {
				columns = ResultRowMappers.getColumns(rs);
			}
		}
		rsLast = rs;
		colsLast = columns;
		return doMap(rs, columns);
	}

	protected T doMap(ResultSet rs, String[] columns) throws SQLException {
		throw new UnsupportedOperationException();
	}
}

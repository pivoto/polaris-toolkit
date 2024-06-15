package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultVoidExtractor implements ResultExtractor<Object> {
	@Override
	public Object extract(ResultSet rs) throws SQLException {
		return null;
	}
}

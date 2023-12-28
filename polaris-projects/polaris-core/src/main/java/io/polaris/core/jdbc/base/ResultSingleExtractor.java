package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultSingleExtractor implements ResultExtractor<Object> {
	@Override
	public Object visit(ResultSet rs) throws SQLException {
		Object o = null;
		if (rs.next()) {
			o = rs.getObject(1);
		}
		return o;
	}
}

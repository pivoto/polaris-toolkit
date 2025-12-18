package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.polaris.core.jdbc.sql.query.Pageable;

/**
 * @author Qt
 */
public class ResultVoidExtractor implements ResultExtractor<Object> {
	@Override
	public Object extract(ResultSet rs) throws SQLException {
		return null;
	}

	@Override
	public Object extract(ResultSet rs, Pageable pageable) throws SQLException {
		return null;
	}
}

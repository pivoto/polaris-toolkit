package io.polaris.core.jdbc.base;


import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since  Feb 06, 2024
 */
public class ResultRowSetVisitor<T> implements ResultSetVisitor {
	private final ResultRowMapper<T> mapper;

	public ResultRowSetVisitor(ResultRowMapper<T> mapper) {
		this.mapper = mapper;
	}

	@Override
	public void visit(ResultSet rs) throws SQLException {

	}
}

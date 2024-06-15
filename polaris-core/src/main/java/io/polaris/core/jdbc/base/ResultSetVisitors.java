package io.polaris.core.jdbc.base;

/**
 * @author Qt
 * @since  Feb 06, 2024
 */
public class ResultSetVisitors {
	public static <T> ResultSetVisitor ofRows(ResultRowMapper<T> mapper, ResultVisitor<T> visitor) {
		return rs -> {
			String[] columns = ResultRowMappers.getColumns(rs);
			while (rs.next()) {
				T t = mapper.map(rs, columns);
				visitor.visit(t);
			}
		};
	}

	public static <T> ResultSetVisitor ofRow(ResultRowMapper<T> mapper, ResultVisitor<T> visitor) {
		return rs -> {
			if (rs.next()) {
				String[] columns = ResultRowMappers.getColumns(rs);
				T t = mapper.map(rs, columns);
				visitor.visit(t);
			}
		};
	}
}

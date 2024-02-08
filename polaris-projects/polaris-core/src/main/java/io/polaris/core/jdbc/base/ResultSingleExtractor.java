package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultSingleExtractor<T> implements ResultExtractor<T> {

	private final ResultRowMapper<T> mapper;

	public ResultSingleExtractor() {
		this(Object.class);
	}

	public ResultSingleExtractor(Type type) {
		this.mapper = ResultRowMappers.ofSingle(type);
	}

	public ResultSingleExtractor(Class<T> type) {
		this.mapper = ResultRowMappers.ofSingle(type);
	}

	@Override
	public T extract(ResultSet rs) throws SQLException {
		if (rs.next()) {
			return  mapper.map(rs, null);
		}
		return null;
	}
}

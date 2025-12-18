package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.polaris.core.jdbc.sql.query.Pageable;

/**
 * @author Qt
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
		return ResultExtractors.extract(rs, mapper, (String[]) null);
	}

	@Override
	public T extract(ResultSet rs, Pageable pageable) throws SQLException {
		return extract(rs);
	}
}

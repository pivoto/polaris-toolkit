package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import io.polaris.core.jdbc.sql.query.Pageable;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultMapExtractor<T extends Map<String, Object>> implements ResultExtractor<T> {

	private final ResultRowMapper<T> mapper;

	@SuppressWarnings("unchecked")
	public ResultMapExtractor() {
		this.mapper = (ResultRowMapper<T>) ResultRowMappers.ofMap();
	}

	public ResultMapExtractor(ResultRowMapper<T> mapper) {
		this.mapper = mapper;
	}

	public ResultMapExtractor(Class<T> mapType) {
		this.mapper = ResultRowMappers.ofMap(mapType);
	}

	@Override
	public T extract(ResultSet rs) throws SQLException {
		return ResultExtractors.extract(rs, mapper);
	}

	@Override
	public T extract(ResultSet rs, Pageable pageable) throws SQLException {
		return extract(rs);
	}
}

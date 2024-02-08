package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultBeanMappingExtractor<T> implements ResultExtractor<T> {

	private final ResultRowMapper<T> mapper;

	public ResultBeanMappingExtractor(BeanMapping<T> mapping) {
		this.mapper = ResultRowMappers.ofMapping(mapping);
	}


	@Override
	public T extract(ResultSet rs) throws SQLException {
		if (rs.next()) {
			return this.mapper.map(rs, null);
		}
		return null;
	}
}

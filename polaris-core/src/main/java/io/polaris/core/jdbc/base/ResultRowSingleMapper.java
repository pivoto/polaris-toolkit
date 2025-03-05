package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.polaris.core.converter.Converters;

/**
 * @author Qt
 * @since  Feb 06, 2024
 */
public class ResultRowSingleMapper<T> extends BaseResultRowMapper<T> {
	private Type type;

	public ResultRowSingleMapper(Type type) {
		this.type = type;
	}
	public ResultRowSingleMapper(Class<T> type) {
		this.type = type;
	}

	@Override
	public T map(ResultSet rs, String[] columns) throws SQLException {
		Object o = BeanMappings.getResultValue(rs, 1, type);
		return Converters.convertQuietly(type, o);
	}

}

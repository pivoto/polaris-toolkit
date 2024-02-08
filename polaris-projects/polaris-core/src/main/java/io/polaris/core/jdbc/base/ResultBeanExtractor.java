package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultBeanExtractor<T> implements ResultExtractor<T> {
	private final ResultRowMapper<T> mapper;

	public ResultBeanExtractor(Class<T> beanType) {
		this(beanType, true, true);
	}

	public ResultBeanExtractor(Type beanType) {
		this(beanType, true, true);
	}

	public ResultBeanExtractor(Class<T> beanType, boolean caseInsensitive, boolean caseCamel) {
		this.mapper = ResultRowMappers.ofBean(beanType, caseInsensitive, caseCamel);
	}

	public ResultBeanExtractor(Type beanType, boolean caseInsensitive, boolean caseCamel) {
		this.mapper = ResultRowMappers.ofBean(beanType, caseInsensitive, caseCamel);
	}


	@Override
	public T extract(ResultSet rs) throws SQLException {
		if (rs.next()) {
			ResultSetMetaData meta = rs.getMetaData();
			int cnt = meta.getColumnCount();
			String[] keys = new String[cnt];
			for (int i = 1; i <= cnt; i++) {
				keys[i - 1] = meta.getColumnLabel(i);
			}
			return this.mapper.map(rs, keys);
		}
		return null;
	}
}

package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

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
		if (rs.next()) {
			ResultSetMetaData meta = rs.getMetaData();
			int cnt = meta.getColumnCount();
			String[] keys = new String[cnt];
			for (int i = 1; i <= cnt; i++) {
				keys[i - 1] = meta.getColumnLabel(i);
			}
			return mapper.map(rs, keys);
		}
		return null;
	}
}

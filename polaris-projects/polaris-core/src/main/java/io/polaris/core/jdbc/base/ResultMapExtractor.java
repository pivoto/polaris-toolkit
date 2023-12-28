package io.polaris.core.jdbc.base;

import io.polaris.core.map.Maps;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultMapExtractor implements ResultExtractor<Map<String, Object>> {
	@Override
	public Map<String, Object> visit(ResultSet rs) throws SQLException {
		if (rs.next()) {
			ResultSetMetaData meta = rs.getMetaData();
			int cnt = meta.getColumnCount();
			String[] keys = new String[cnt];
			for (int i = 1; i <= cnt; i++) {
				keys[i - 1] = meta.getColumnLabel(i);
			}
			Map<String, Object> map = Maps.newUpperCaseLinkedHashMap();
			for (int i = 1; i <= cnt; i++) {
				String key = keys[i - 1];
				map.put(key, rs.getObject(i));
			}
			return map;
		}
		return null;
	}
}

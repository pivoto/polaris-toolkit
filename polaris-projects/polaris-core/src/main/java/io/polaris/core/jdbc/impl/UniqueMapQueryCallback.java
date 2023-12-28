package io.polaris.core.jdbc.impl;

import io.polaris.core.jdbc.QueryCallback;
import io.polaris.core.map.Maps;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class UniqueMapQueryCallback implements QueryCallback<Map<String, Object>> {
	@Override
	public Map<String, Object> visit(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int cnt = meta.getColumnCount();
		Map<String, Object> map = Maps.newUpperCaseLinkedHashMap();
		if (rs.next()) {
			for (int i = 1; i <= cnt; i++) {
				map.put(meta.getColumnLabel(i).toUpperCase(), rs.getObject(i));
			}
		}
		return map;
	}
}

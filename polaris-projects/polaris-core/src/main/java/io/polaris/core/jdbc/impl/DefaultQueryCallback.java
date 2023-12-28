package io.polaris.core.jdbc.impl;

import io.polaris.core.jdbc.QueryCallback;
import io.polaris.core.map.Maps;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class DefaultQueryCallback implements QueryCallback<List<Map<String, Object>>> {
	@Override
	public List<Map<String, Object>> visit(ResultSet rs) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		ResultSetMetaData meta = rs.getMetaData();
		int cnt = meta.getColumnCount();
		while (rs.next()) {
			Map<String, Object> map = Maps.newUpperCaseLinkedHashMap();
			for (int i = 1; i <= cnt; i++) {
				map.put(meta.getColumnLabel(i).toUpperCase(), rs.getObject(i));
			}
			list.add(map);
		}
		return list;
	}
}

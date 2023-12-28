package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultBeanMappingListExtractor<T> implements ResultExtractor<List<T>> {

	private final BeanMapping<T> mapping;

	public ResultBeanMappingListExtractor(BeanMapping<T> mapping) {
		mapping.prepare();
		this.mapping = mapping;
	}


	@Override
	public List<T> visit(ResultSet rs) throws SQLException {
		List<T> list = new ArrayList<>();
		ResultSetMetaData meta = rs.getMetaData();
		int cnt = meta.getColumnCount();
		Map<String, Integer> cols = new HashMap<>();
		for (int i = 1; i <= cnt; i++) {
			cols.put(meta.getColumnLabel(i), i);
		}
		BeanMappingKit.removeMappingCols(cols, mapping);
		int propertyCaseModel = mapping.getPropertyCaseModel();
		while (rs.next()) {
			T bean = mapping.getMetaObject().newInstance();
			for (Map.Entry<String, Integer> entry : cols.entrySet()) {
				mapping.getMetaObject().setPathProperty(bean, propertyCaseModel, entry.getKey(), rs.getObject(entry.getValue()));
			}
			BeanMappingKit.setProperty(rs, mapping, propertyCaseModel, bean);
			list.add(bean);
		}
		return list;
	}
}

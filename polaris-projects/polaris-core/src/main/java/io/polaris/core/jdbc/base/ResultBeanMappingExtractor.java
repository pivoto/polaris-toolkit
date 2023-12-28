package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultBeanMappingExtractor<T> implements ResultExtractor<T> {

	private final BeanMapping<T> mapping;

	public ResultBeanMappingExtractor(BeanMapping<T> mapping) {
		mapping.prepare();
		this.mapping = mapping;
	}


	@Override
	public T visit(ResultSet rs) throws SQLException {
		if (rs.next()) {
			ResultSetMetaData meta = rs.getMetaData();
			int cnt = meta.getColumnCount();
			Map<String, Integer> cols = new HashMap<>();
			for (int i = 1; i <= cnt; i++) {
				cols.put(meta.getColumnLabel(i), i);
			}
			BeanMappingKit.removeMappingCols(cols, mapping);
			T bean = mapping.getMetaObject().newInstance();
			int propertyCaseModel = mapping.getPropertyCaseModel();
			for (Map.Entry<String, Integer> entry : cols.entrySet()) {
				mapping.getMetaObject().setPathProperty(bean, propertyCaseModel, entry.getKey(), rs.getObject(entry.getValue()));
			}
			BeanMappingKit.setProperty(rs, mapping, propertyCaseModel, bean);
			return bean;
		}
		return null;
	}
}

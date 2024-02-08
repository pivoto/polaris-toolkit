package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
public class ResultRowBeanMappingMapper<T> extends BaseResultRowMapper<T> {

	private final BeanMapping<T> mapping;
	private String[] colsLast;
	private Map<String, Integer> unmappedCols;

	public ResultRowBeanMappingMapper(BeanMapping<T> mapping) {
		mapping.prepare();
		this.mapping = mapping;
	}

	@Override
	protected T doMap(ResultSet rs, String[] columns) throws SQLException {
		if (colsLast != columns) {
			Map<String, Integer> cols = new HashMap<>();
			for (int i = 1; i <= columns.length; i++) {
				cols.put(columns[i - 1], i);
			}
			BeanMappingKit.removeMappingCols(cols, mapping);
			unmappedCols = Collections.unmodifiableMap(cols);
			colsLast = columns;
		}
		int caseModel = mapping.getCaseModel();
		T bean = mapping.getMetaObject().newInstance();
		for (Map.Entry<String, Integer> entry : unmappedCols.entrySet()) {
			String key = entry.getKey();
			Object val = rs.getObject(entry.getValue());
			mapping.getMetaObject().setPathProperty(bean, caseModel, key, val);
		}
		BeanMappingKit.setProperty(rs, mapping, caseModel, bean);
		return bean;
	}

}

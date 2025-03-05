package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.polaris.core.lang.bean.CaseModeOption;
import io.polaris.core.lang.bean.MetaObject;

/**
 * @author Qt
 * @since Feb 06, 2024
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
			BeanMappings.removeMappingCols(cols, mapping);
			unmappedCols = Collections.unmodifiableMap(cols);
			colsLast = columns;
		}
		CaseModeOption caseModel = mapping.getCaseMode();
		T bean = mapping.getMetaObject().newInstance();
		if (bean != null) {
			for (Map.Entry<String, Integer> entry : unmappedCols.entrySet()) {
				String key = entry.getKey();
				Integer columnIndex = entry.getValue();
				// 防止数据库驱动获取的对象类型无法正常转换，这里对常见类型做提前判定
				MetaObject<?> valMeta = mapping.getMetaObject().getPathProperty(caseModel,key);
				Object val = BeanMappings.getResultValue(rs, columnIndex, valMeta);
				mapping.getMetaObject().setPathProperty(bean, caseModel, key, val);
			}
		}
		BeanMappings.setProperty(rs, mapping, caseModel, bean);
		return bean;
	}

}

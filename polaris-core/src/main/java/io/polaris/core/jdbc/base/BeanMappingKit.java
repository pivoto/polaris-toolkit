package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import io.polaris.core.lang.bean.CaseModeOption;
import io.polaris.core.lang.bean.MetaObject;

/**
 * @author Qt
 * @since  Dec 28, 2023
 */
class BeanMappingKit {

	@SuppressWarnings("rawtypes")
	public static <T> void setProperty(ResultSet rs, BeanMapping<T> mapping, CaseModeOption caseModel, T bean) throws SQLException {
		MetaObject<T> metaObject = mapping.getMetaObject();

		if (mapping.getColumns() != null) {
			for (BeanPropertyMapping pm : mapping.getColumns()) {
				Object val = rs.getObject(pm.getColumn());
				if (val != null) {
					metaObject.setProperty(bean, caseModel, pm.getProperty(), val);
				}
			}
		}

		if (mapping.getComposites() != null) {
			for (BeanCompositeMapping<?> composite : mapping.getComposites()) {
				String subProperty = composite.getProperty();
				BeanMapping subMapping = composite.getMapping();
				MetaObject subMetaObject = subMapping.getMetaObject();
				if (subMetaObject != null) {
					Object subBean = metaObject.getProperty(bean, caseModel, subProperty);
					if (subBean == null) {
						subBean = subMetaObject.newInstance();
						metaObject.setProperty(bean ,caseModel, subProperty,subBean);
					}
					setProperty(rs, subMapping, caseModel, subBean);
				}
			}
		}
	}

	public static <T> void removeMappingCols(Map<String, Integer> resultCols, BeanMapping<T> mapping) {
		if (mapping == null) {
			return;
		}
		if (mapping.getColumns() != null) {
			for (BeanPropertyMapping pm : mapping.getColumns()) {
				resultCols.remove(pm.getColumn());
			}
		}
		if (mapping.getComposites() != null) {
			for (BeanCompositeMapping<?> composite : mapping.getComposites()) {
				removeMappingCols(resultCols, composite.getMapping());
			}
		}
	}

	public static <T> void markMappingCols(Map<String, Boolean> resultCols, BeanMapping<T> mapping) {
		if (mapping == null) {
			return;
		}
		if (mapping.getColumns() != null) {
			for (BeanPropertyMapping pm : mapping.getColumns()) {
				if (resultCols.containsKey(pm.getColumn())) {
					resultCols.put(pm.getColumn(), true);
				}
			}
		}
		if (mapping.getComposites() != null) {
			for (BeanCompositeMapping<?> composite : mapping.getComposites()) {
				markMappingCols(resultCols, composite.getMapping());
			}
		}
	}
}

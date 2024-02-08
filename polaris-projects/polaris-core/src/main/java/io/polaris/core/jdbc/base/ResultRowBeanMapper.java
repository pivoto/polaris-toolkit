package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.polaris.core.lang.bean.MetaObject;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
public class ResultRowBeanMapper<T> extends BaseResultRowMapper<T> {
	private final MetaObject<T> metaObject;
	private final int caseModel;

	public ResultRowBeanMapper(Class<T> beanType) {
		this(beanType, true, true);
	}

	public ResultRowBeanMapper(Class<T> beanType, boolean caseInsensitive, boolean caseCamel) {
		this.metaObject = MetaObject.of(beanType);
		this.caseModel = MetaObject.buildCaseModel(caseInsensitive, caseCamel);
	}
	public ResultRowBeanMapper(Type beanType) {
		this(beanType, true, true);
	}

	public ResultRowBeanMapper(Type beanType, boolean caseInsensitive, boolean caseCamel) {
		this.metaObject = MetaObject.of(beanType);
		this.caseModel = MetaObject.buildCaseModel(caseInsensitive, caseCamel);
	}


	@Override
	protected T doMap(ResultSet rs, String[] columns) throws SQLException {
		T bean = metaObject.newInstance();
		for (int i = 1; i <= columns.length; i++) {
			String key = columns[i - 1];
			metaObject.setPathProperty(bean, caseModel, key, rs.getObject(i));
		}
		return bean;
	}

}

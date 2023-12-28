package io.polaris.core.jdbc.base;

import io.polaris.core.lang.bean.MetaObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultBeanListExtractor<T> implements ResultExtractor<List<T>> {
	private final MetaObject<T> metaObject;
	private final int propertyCaseModel;

	public ResultBeanListExtractor(Class<T> beanType) {
		this(beanType, true, true);
	}

	public ResultBeanListExtractor(Class<T> beanType, boolean caseInsensitive, boolean caseCamel) {
		this.metaObject = MetaObject.of(beanType);
		this.propertyCaseModel = MetaObject.buildPropertyCaseModel(caseInsensitive, caseCamel);
	}


	@Override
	public List<T> visit(ResultSet rs) throws SQLException {
		List<T> list = new ArrayList<>();

		ResultSetMetaData meta = rs.getMetaData();
		int cnt = meta.getColumnCount();
		String[] keys = new String[cnt];
		for (int i = 1; i <= cnt; i++) {
			keys[i - 1] = meta.getColumnLabel(i);
		}

		while (rs.next()) {
			T bean = metaObject.newInstance();
			for (int i = 1; i <= cnt; i++) {
				String key = keys[i - 1];
				metaObject.setPathProperty(bean, propertyCaseModel, key, rs.getObject(i));
			}
			list.add(bean);
		}
		return list;
	}
}

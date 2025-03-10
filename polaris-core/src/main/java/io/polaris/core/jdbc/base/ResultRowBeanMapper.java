package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.polaris.core.lang.bean.CaseModeOption;
import io.polaris.core.lang.bean.MetaObject;

/**
 * @author Qt
 * @since Feb 06, 2024
 */
public class ResultRowBeanMapper<T> extends BaseResultRowMapper<T> {
	private final MetaObject<T> metaObject;
	private final CaseModeOption caseMode;

	public ResultRowBeanMapper(Class<T> beanType) {
		this(beanType, CaseModeOption.all());
	}

	public ResultRowBeanMapper(Class<T> beanType, CaseModeOption caseMode) {
		this.metaObject = MetaObject.of(beanType);
		this.caseMode = caseMode;
	}

	public ResultRowBeanMapper(Type beanType) {
		this(beanType, CaseModeOption.all());
	}

	public ResultRowBeanMapper(Type beanType, CaseModeOption caseMode) {
		this.metaObject = MetaObject.of(beanType);
		this.caseMode = caseMode;
	}

	@Override
	protected T doMap(ResultSet rs, String[] columns) throws SQLException {
		T bean = metaObject.newInstance();
		if (bean != null) {
			for (int i = 1; i <= columns.length; i++) {
				String key = columns[i - 1];
				// 防止数据库驱动获取的对象类型无法正常转换，这里对常见类型做提前判定
				MetaObject<?> valMeta = metaObject.getPathProperty(caseMode, key);
				Object val = BeanMappings.getResultValue(rs, i, valMeta);
				metaObject.setPathProperty(bean, caseMode, key, val);
			}
		}
		return bean;
	}

}

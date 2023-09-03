package io.polaris.mybatis.support;

import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.statement.Statements;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.bean.Beans;

import java.util.function.Predicate;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
public class MybatisToolkit {

	public static boolean isNotEmpty(Object val) {
		return Objs.isNotEmpty(val);
	}

	public static boolean isLikeString(Object val) {
		if (val instanceof String) {
			return ((String) val).startsWith("%") || ((String) val).endsWith("%");
		}
		return false;
	}

	/**
	 * 是否有指定的级联属性
	 *
	 * @param _parameter
	 * @param propertyName 级联属性
	 * @return
	 */
	public static boolean hasPathProperty(Object _parameter, String propertyName) {
		try {
			Object val = Beans.getPathProperty(_parameter, propertyName);
			return isNotEmpty(val);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 是否有指定的属性
	 *
	 * @param _parameter
	 * @param propertyName 属性
	 * @return
	 */
	public static boolean hasProperty(Object _parameter, String propertyName) {
		try {
			Object val = Beans.getProperty(_parameter, propertyName);
			return isNotEmpty(val);
		} catch (Exception e) {
			return false;
		}
	}


	public static TableMeta getTableMeta(String entityClassName) {
		return Statements.getTableMeta(entityClassName);
	}


}

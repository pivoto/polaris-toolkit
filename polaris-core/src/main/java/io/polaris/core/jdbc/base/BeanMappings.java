package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.TemporalAccessor;
import java.util.Map;

import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.CaseModeOption;
import io.polaris.core.lang.bean.MetaObject;

/**
 * @author Qt
 * @since Dec 28, 2023
 */
public class BeanMappings {

	@SuppressWarnings("rawtypes")
	public static <T> void setProperty(ResultSet rs, BeanMapping<T> mapping, CaseModeOption caseModel, T bean) throws SQLException {
		MetaObject<T> metaObject = mapping.getMetaObject();

		if (mapping.getColumns() != null) {
			for (BeanPropertyMapping pm : mapping.getColumns()) {
				// 防止数据库驱动获取的对象类型无法正常转换，这里对常见类型做提前判定
				MetaObject<?> valMeta = metaObject.getProperty(caseModel, pm.getProperty());
				Object val = getResultValue(rs, pm.getColumn(), valMeta);
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
						metaObject.setProperty(bean, caseModel, subProperty, subBean);
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


	public static Object getResultValue(ResultSet rs, int column, MetaObject<?> meta) throws SQLException {
		if (meta == null) {
			return rs.getObject(column);
		}
		JavaType<?> beanType = meta.getBeanType();
		Class<?> clazz = beanType.getRawClass();
		return getResultValue(rs, column, clazz);
	}

	public static Object getResultValue(ResultSet rs, String column, MetaObject<?> meta) throws SQLException {
		if (meta == null) {
			return rs.getObject(column);
		}
		JavaType<?> beanType = meta.getBeanType();
		Class<?> clazz = beanType.getRawClass();
		return getResultValue(rs, column, clazz);
	}

	public static Object getResultValue(ResultSet rs, int col, Type type) throws SQLException {
		if (type == null) {
			return rs.getObject(col);
		}
		Class<?> clazz = Types.getClass(type);
		if (clazz.equals(Object.class)) {
			return rs.getObject(col);
		}
		if (clazz.equals(String.class)
			|| clazz.isEnum()
			|| CharSequence.class.isAssignableFrom(clazz)
		) {
			return rs.getString(col);
		}
		if (clazz.equals(int.class)) {
			return rs.getInt(col);
		}
		if (clazz.equals(Integer.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.intValue();
		}
		if (clazz.equals(long.class)) {
			return rs.getLong(col);
		}
		if (clazz.equals(Long.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.longValue();
		}
		if (clazz.equals(boolean.class)) {
			return rs.getBoolean(col);
		}
		if (clazz.equals(Boolean.class)) {
			String val = rs.getString(col);
			if (val == null) {
				return null;
			}
			return rs.getBoolean(col);
		}

		if (clazz.equals(double.class)) {
			return rs.getDouble(col);
		}
		if (clazz.equals(Double.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.doubleValue();
		}
		if (clazz.equals(float.class)) {
			return rs.getFloat(col);
		}
		if (clazz.equals(Float.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.floatValue();
		}
		if (clazz.equals(byte.class)) {
			return rs.getByte(col);
		}
		if (clazz.equals(Byte.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.byteValue();
		}
		if (clazz.equals(short.class)) {
			return rs.getShort(col);
		}
		if (clazz.equals(Short.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.shortValue();
		}
		if (clazz.equals(char.class)) {
			String val = rs.getString(col);
			return val == null || val.isEmpty() ? '\0' : val.charAt(0);
		}
		if (clazz.equals(Character.class)) {
			String val = rs.getString(col);
			return val == null || val.isEmpty() ? null : val.charAt(0);
		}

		if (Number.class.isAssignableFrom(clazz)) {
			return rs.getBigDecimal(col);
		}
		if (byte[].class.isAssignableFrom(clazz)) {
			return rs.getBytes(col);
		}
		if (java.sql.Timestamp.class.isAssignableFrom(clazz)) {
			return rs.getTimestamp(col);
		}
		if (java.sql.Time.class.isAssignableFrom(clazz)) {
			return rs.getTime(col);
		}
		if (java.util.Date.class.isAssignableFrom(clazz)) {
			return rs.getDate(col);
		}
		if (TemporalAccessor.class.isAssignableFrom(clazz)) {
			return rs.getTimestamp(col);
		}
		try {
			return rs.getObject(col, clazz);
		} catch (Throwable e) {
			return rs.getObject(col);
		}
	}

	public static Object getResultValue(ResultSet rs, String col, Type type) throws SQLException {
		if (type == null) {
			return rs.getObject(col);
		}
		Class<?> clazz = Types.getClass(type);
		if (clazz.equals(Object.class)) {
			return rs.getObject(col);
		}
		if (clazz.equals(String.class)
			|| clazz.isEnum()
			|| CharSequence.class.isAssignableFrom(clazz)
		) {
			return rs.getString(col);
		}
		if (clazz.equals(int.class)) {
			return rs.getInt(col);
		}
		if (clazz.equals(Integer.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.intValue();
		}
		if (clazz.equals(long.class)) {
			return rs.getLong(col);
		}
		if (clazz.equals(Long.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.longValue();
		}
		if (clazz.equals(boolean.class)) {
			return rs.getBoolean(col);
		}
		if (clazz.equals(Boolean.class)) {
			String val = rs.getString(col);
			if (val == null) {
				return null;
			}
			return rs.getBoolean(col);
		}

		if (clazz.equals(double.class)) {
			return rs.getDouble(col);
		}
		if (clazz.equals(Double.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.doubleValue();
		}
		if (clazz.equals(float.class)) {
			return rs.getFloat(col);
		}
		if (clazz.equals(Float.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.floatValue();
		}
		if (clazz.equals(byte.class)) {
			return rs.getByte(col);
		}
		if (clazz.equals(Byte.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.byteValue();
		}
		if (clazz.equals(short.class)) {
			return rs.getShort(col);
		}
		if (clazz.equals(Short.class)) {
			BigDecimal val = rs.getBigDecimal(col);
			if (val == null) {
				return null;
			}
			return val.shortValue();
		}
		if (clazz.equals(char.class)) {
			String val = rs.getString(col);
			return val == null || val.isEmpty() ? '\0' : val.charAt(0);
		}
		if (clazz.equals(Character.class)) {
			String val = rs.getString(col);
			return val == null || val.isEmpty() ? null : val.charAt(0);
		}

		if (Number.class.isAssignableFrom(clazz)) {
			return rs.getBigDecimal(col);
		}
		if (byte[].class.isAssignableFrom(clazz)) {
			return rs.getBytes(col);
		}
		if (java.sql.Timestamp.class.isAssignableFrom(clazz)) {
			return rs.getTimestamp(col);
		}
		if (java.sql.Time.class.isAssignableFrom(clazz)) {
			return rs.getTime(col);
		}
		if (java.util.Date.class.isAssignableFrom(clazz)) {
			return rs.getDate(col);
		}
		if (TemporalAccessor.class.isAssignableFrom(clazz)) {
			return rs.getTimestamp(col);
		}
		try {
			return rs.getObject(col, clazz);
		} catch (Throwable e) {
			return rs.getObject(col);
		}
	}
}

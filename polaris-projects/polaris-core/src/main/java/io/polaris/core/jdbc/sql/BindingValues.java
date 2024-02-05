package io.polaris.core.jdbc.sql;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.ValueRef;

/**
 * @author Qt
 * @since 1.8,  Jan 30, 2024
 */
public class BindingValues {

	public static Object getValueForInsert(ColumnMeta meta, Object val) {
		if (val == null) {
			if (meta.isCreateTime() || meta.isUpdateTime()) {
				val = Converters.convertQuietly(meta.getFieldType(), new Date());
			}
		}
		if (val == null) {
			String insertDefault = meta.getInsertDefault();
			if (Strings.isNotBlank(insertDefault)) {
				val = Converters.convertQuietly(meta.getFieldType(), insertDefault);
			}
		}
		if (val == null) {
			if (meta.isVersion()) {
				val = Converters.convertQuietly(meta.getFieldType(), 1L);
			}
		}
		if (val == null) {
			if (meta.isLogicDeleted()) {
				val = Converters.convertQuietly(meta.getFieldType(), false);
			}
		}
		return val;
	}

	public static Object getValueForUpdate(ColumnMeta meta, Object val) {
		if (val == null) {
			if (meta.isUpdateTime()) {
				val = Converters.convertQuietly(meta.getFieldType(), new Date());
			}
		}
		if (val == null) {
			String updateDefault = meta.getUpdateDefault();
			if (Strings.isNotBlank(updateDefault)) {
				val = Converters.convertQuietly(meta.getFieldType(), updateDefault);
			}
		}
		return val;
	}

	/**
	 * 提取日期范围的查询条件，值对象必须是两个元素的数组或集合，且不能都是空元素。
	 * 存在则返回两元素数组，否则返回Null
	 */
	@Nullable
	public static Date[] getDateRangeOrNull(Object val) {
		Date[] range = new Date[2];
		// 两个元素的日期类字段特殊处理，认为是日期范围条件
		Object[] couple = new Object[2];
		if (val instanceof Iterable) {
			Iterator<?> iter = ((Iterable<?>) val).iterator();
			if (iter.hasNext()) {
				Object next = iter.next();
				couple[0] = next;
			}
			if (iter.hasNext()) {
				Object next = iter.next();
				couple[1] = next;
			}
			if (iter.hasNext()) {
				return null;
			}
		} else if (val.getClass().isArray()) {
			int len = Array.getLength(val);
			if (len == 2) {
				Object start = Array.get(val, 0);
				Object end = Array.get(val, 1);
				couple[0] = start;
				couple[1] = end;
			} else {
				return null;
			}
		}

		if (couple[0] == null && couple[1] == null) {
			return null;
		}
		range[0] = Converters.convertQuietly(Date.class, couple[0]);
		range[1] = Converters.convertQuietly(Date.class, couple[1]);
		if (range[0] == null && range[1] == null) {
			return null;
		}
		return range;
	}

	public static Object getDefaultTimeVal(Class<?> fieldType) {
		if (fieldType.isAssignableFrom(Date.class)) {
			return new Date();
		}
		if (fieldType.isAssignableFrom(Timestamp.class)) {
			return new Timestamp(System.currentTimeMillis());
		}
		if (fieldType.isAssignableFrom(java.sql.Date.class)) {
			return new Timestamp(System.currentTimeMillis());
		}
		if (fieldType.isAssignableFrom(LocalDateTime.class)) {
			return LocalDateTime.now();
		}
		if (fieldType.isAssignableFrom(LocalDate.class)) {
			return LocalDate.now();
		}
		return new Date();
	}

	public static Object getBindingValueOrDefault(Map<String, Object> bindings, String key, Object defVal) {
		if (bindings == null) {
			return defVal;
		}
		if (key.contains(".") || key.contains("[")) {
			Object val = Beans.getPathProperty(bindings, key);
			if (val == null) {
				return defVal;
			}
			return val;
		}
		return bindings.getOrDefault(key, defVal);
	}

	public static Object getBindingValueOrDefault(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, String key, Object defVal) {
		if (bindings == null) {
			return defVal;
		}
		Object val;
		if (cache != null) {
			ValueRef<Object> ref = cache.get(key);
			if (ref != null) {
				val = ref.get();
			} else {
				if (key.contains(".") || key.contains("[")) {
					val = Beans.getPathProperty(bindings, key);
					cache.put(key, ValueRef.of(val));
				} else {
					val = bindings.get(key);
					cache.put(key, ValueRef.of(val));
				}
			}
		} else {
			if (key.contains(".") || key.contains("[")) {
				val = Beans.getPathProperty(bindings, key);
			} else {
				val = bindings.get(key);
			}
		}
		if (val == null) {
			return defVal;
		}
		return val;
	}

}

package io.polaris.core.jdbc.sql;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import io.polaris.core.consts.StdConsts;
import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.string.StringCases;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.ValueRef;

/**
 * @author Qt
 * @since Jan 30, 2024
 */
public class BindingValues {

	public static Object getValueForInsert(ColumnMeta meta, Object val) {
		if (val == null) {
			if (meta.isCreateTime() || meta.isUpdateTime()) {
				val = Converters.convertQuietly(meta.getFieldType(), new java.sql.Timestamp(System.currentTimeMillis()));
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
				val = Converters.convertQuietly(meta.getFieldType(), new java.sql.Timestamp(System.currentTimeMillis()));
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
		if (fieldType.isAssignableFrom(Timestamp.class)) {
			return new Timestamp(System.currentTimeMillis());
		}
		if (fieldType.isAssignableFrom(java.sql.Date.class)) {
			return new java.sql.Date(System.currentTimeMillis());
		}
		if (fieldType.isAssignableFrom(Date.class)) {
			return new Date();
		}
		if (fieldType.isAssignableFrom(LocalDateTime.class)) {
			return LocalDateTime.now();
		}
		if (fieldType.isAssignableFrom(LocalDate.class)) {
			return LocalDate.now();
		}
		return new Date();
	}

	public static Object getBindingValueOrNull(Map<String, Object> bindings, String key) {
		return getBindingValueOrDefault(bindings, key, null);
	}

	public static Object getBindingValueOrDefault(Map<String, Object> bindings, String key, Object defVal) {
		if (bindings == null) {
			return defVal;
		}
		// 考虑如 org.apache.ibatis.binding.MapperMethod.ParamMap 等取值时可能存在异常，需要处理异常
		try {
			if (key.contains(".") || key.contains("[")) {
				Object val = Beans.getPathProperty(bindings, key);
				// 降级处理
				if (val == null) {
					return bindings.getOrDefault(key, defVal);
				}
				return val;
			}
			return bindings.getOrDefault(key, defVal);
		} catch (Exception e) {
			return defVal;
		}
	}

	public static Object getBindingValueOrNull(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, String key) {
		return getBindingValueOrDefault(cache, bindings, key, null);
	}

	public static Object getBindingValueOrDefault(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, String key, Object defVal) {
		if (bindings == null) {
			return defVal;
		}
		// 考虑如 org.apache.ibatis.binding.MapperMethod.ParamMap 等取值时可能存在异常，需要处理异常
		Object val;
		if (cache != null) {
			ValueRef<Object> ref = cache.get(key);
			if (ref != null) {
				val = ref.get();
			} else {
				if (key.contains(".") || key.contains("[")) {
					try {
						val = Beans.getPathProperty(bindings, key);
						// 降级处理
						if (val == null) {
							val = bindings.get(key);
						}
					} catch (Exception e) {
						val = null;
					}
					cache.put(key, ValueRef.of(val));
				} else {
					try {
						val = bindings.get(key);
					} catch (Exception e) {
						val = null;
					}
					cache.put(key, ValueRef.of(val));
				}
			}
		} else {
			try {
				if (key.contains(".") || key.contains("[")) {
					val = Beans.getPathProperty(bindings, key);
					// 降级处理
					if (val == null) {
						val = bindings.get(key);
					}
				} else {
					val = bindings.get(key);
				}
			} catch (Exception e) {
				val = null;
			}
		}
		if (val == null) {
			return defVal;
		}
		return val;
	}


	/**
	 * 是否基本数据类型(基本类型,枚举,数组,字符串,数值,日期)
	 *
	 * @param clazz
	 * @return
	 */
	private static boolean isBasicDataType(Class clazz) {
		return clazz.isPrimitive()
			|| clazz.isEnum()
			|| clazz.isArray()
			|| String.class == clazz
			|| Integer.class == clazz
			|| BigDecimal.class == clazz
			|| Double.class == clazz
			|| BigInteger.class == clazz
			|| Float.class == clazz
			|| Short.class == clazz
			|| Byte.class == clazz
			|| Character.class == clazz
			|| Boolean.class == clazz
			|| Date.class.isAssignableFrom(clazz)
			;
	}

	public static Map<String, Object> asMap(Object o) {
		Map<String, Object> params = new HashMap<>();
		// 兼容小写驼峰转小写下划线
		Map<String, Object> compatible = new HashMap<>();
		if (o == null) {
			params.put(StdConsts.VALUE, null);
		} else if (isBasicDataType(o.getClass())) {
			params.put(StdConsts.VALUE, o);
		} else if (o instanceof Map) {
			// 遍历KeySet, 不使用entrySet, 以适应某些特殊形式的Map支持
			Set keys = ((Map) o).keySet();
			for (Object key : keys) {
				String s = Objects.toString(key, null);
				if (s != null) {
					Object val = ((Map) o).get(key);
					params.put(s, val);
					String underlineKey = StringCases.camelToUnderlineCase(s);
					if (!s.equals(underlineKey)) {
						compatible.put(underlineKey, val);
					}
				}
			}
		} else if (o.getClass() != Object.class) {
			try {
				BeanMap<Object> map = Beans.newBeanMap(o);
				for (String key : map.keySet()) {
					Object val = map.get(key);
					params.put(key, val);
					String underlineKey = StringCases.camelToUnderlineCase(key);
					if (!key.equals(underlineKey)) {
						compatible.put(underlineKey, val);
					}
				}
			} catch (Exception e) {
			}
		} else {
			params.put(StdConsts.VALUE, o);
		}
		if (!compatible.isEmpty()) {
			for (Map.Entry<String, Object> entry : compatible.entrySet()) {
				params.putIfAbsent(entry.getKey(), entry.getValue());
			}
		}
		return params;
	}


	public static String asSqlWithBindings(Map<String, Object> map, SqlNodeBuilder sqlNodeBuilder) {
		SqlNode sqlNode = sqlNodeBuilder.toSqlNode();
		return asSqlWithBindings(map, sqlNode);
	}


	public static String asSqlWithBindings(Map<String, Object> map, SqlNode sqlNode) {
		BoundSql boundSql = sqlNode.asBoundSql();
		Map<String, Object> bindings = boundSql.getBindings();
		if (bindings != null && !bindings.isEmpty()) {
			map.putAll(bindings);
		}
		return boundSql.getText();
	}

}

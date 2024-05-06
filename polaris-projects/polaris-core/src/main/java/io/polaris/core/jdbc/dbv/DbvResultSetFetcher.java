package io.polaris.core.jdbc.dbv;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.dbv.annotation.DbvColumn;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.CaseInsensitiveMap;
import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since 1.8
 */
public class DbvResultSetFetcher {
	private static final ILogger log = ILoggers.of(DbvResultSetFetcher.class);

	public static Map<String, Object> fetch(final ResultSet rs) throws SQLException {
		final ResultSetMetaData meta = rs.getMetaData();
		final int cnt = meta.getColumnCount();
		final Map<String, Object> map = new CaseInsensitiveMap<>(new LinkedHashMap<>(), true);
		for (int i = 1; i <= cnt; i++) {
			map.put(meta.getColumnLabel(i).toUpperCase(), rs.getObject(i));
		}
		return map;
	}

	public static List<Map<String, Object>> fetchList(final ResultSet rs)
		throws SQLException {
		final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			list.add(fetch(rs));
		}
		return list;
	}

	public static Object fetch(final ResultSet rs, final Object obj) throws SQLException {
		DbvColumnFieldsMeta columnFields = getColumnFieldsMetadata(rs, obj.getClass());
		return fetch(columnFields, rs, obj);
	}

	public static <T> List<T> fetchList(final ResultSet rs, final Class<T> clazz)
		throws SQLException, ReflectiveOperationException {
		DbvColumnFieldsMeta columnFields = getColumnFieldsMetadata(rs, clazz);
		final List<T> list = new ArrayList<T>();
		while (rs.next()) {
			final T object = clazz.newInstance();
			fetch(columnFields, rs, object);
			list.add(object);
		}
		return list;
	}

	public static Object fetch(DbvColumnFieldsMeta columnFields, ResultSet rs, Object obj) {
		Set<DbvColumnFieldMeta> fields = columnFields.getFields();
		for (DbvColumnFieldMeta meta : fields) {
			Field field = meta.getField();
			DbvColumnGetter handler = meta.getGetter();
			boolean accessible = field.isAccessible();
			try {
				field.setAccessible(true);
				field.set(obj, handler.getColumnValue(rs, meta.getColumn()));

			} catch (SQLException | IllegalAccessException e) {
				log.debug(e.getMessage(), e);
			} finally {
				field.setAccessible(accessible);
			}
		}
		return obj;
	}

	public static Set<String> getColumnNames(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();
		Set<String> colSet = new LinkedHashSet<>(columnCount);
		for (int i = 1; i <= columnCount; i++) {
			colSet.add(meta.getColumnName(i).toUpperCase());
		}
		return colSet;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static DbvColumnFieldsMeta getColumnFieldsMetadata(ResultSet resultSet, Class clazz) throws SQLException {
		DbvColumnFieldsMeta metadata = new DbvColumnFieldsMeta();
		Set<String> columnNames = getColumnNames(resultSet);

		Set<DbvColumnFieldMeta> fields = new LinkedHashSet<>();

		for (Class<?> superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
			Field[] declaredFields = superClass.getDeclaredFields();
			for (Field field : declaredFields) {
				if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())
					|| Modifier.isTransient(field.getModifiers())) {
					continue;
				}
				String columnName;
				DbvColumnGetter getter = null;
				final DbvColumn columnNameAnno = field.getAnnotation(DbvColumn.class);
				if (columnNameAnno == null) {
					columnName = castFieldToColumn(field.getName());
				} else {
					columnName = columnNameAnno.value().toUpperCase();
					Class<? extends DbvColumnGetter> getterClass = columnNameAnno.getter();
					if (getterClass != DbvColumnGetter.class && !Modifier.isAbstract(getterClass.getModifiers())) {
						try {
							getter = Reflects.newInstance(getterClass);
						} catch (ReflectiveOperationException ignored) {
						}
					}
				}
				if (!columnNames.contains(columnName)) {
					continue;
				}
				DbvColumnFieldMeta meta = new DbvColumnFieldMeta();
				meta.setColumn(columnName);
				meta.setField(field);
				fields.add(meta);

				if (getter != null) {
					meta.setGetter(getter);
				} else {
					final Class<?> type = field.getType();
					if (String.class == type) {
						meta.setGetter(ResultSet::getString);
					} else if (int.class == type) {
						meta.setGetter(ResultSet::getInt);
					} else if (Integer.class == type) {
						meta.setGetter((rs, col) -> {
							BigDecimal val = rs.getBigDecimal(col);
							if (val == null) {
								return null;
							}
							return val.intValue();
						});
					} else if (long.class == type) {
						meta.setGetter(ResultSet::getLong);
					} else if (Long.class == type) {
						meta.setGetter((rs, col) -> {
							BigDecimal val = rs.getBigDecimal(col);
							if (val == null) {
								return null;
							}
							return val.longValue();
						});
					} else if (boolean.class == type) {
						meta.setGetter(ResultSet::getBoolean);
					} else if (Boolean.class == type) {
						meta.setGetter((rs, col) -> {
							String val = rs.getString(col);
							if (val == null) {
								return null;
							}
							return rs.getBoolean(col);
						});
					} else if (double.class == type) {
						meta.setGetter(ResultSet::getDouble);
					} else if (Double.class == type) {
						meta.setGetter((rs, col) -> {
							BigDecimal val = rs.getBigDecimal(col);
							if (val == null) {
								return null;
							}
							return val.doubleValue();
						});
					} else if (float.class == type) {
						meta.setGetter(ResultSet::getFloat);
					} else if (Float.class == type) {
						meta.setGetter((rs, col) -> {
							BigDecimal val = rs.getBigDecimal(col);
							if (val == null) {
								return null;
							}
							return val.floatValue();
						});
					} else if (byte.class == type) {
						meta.setGetter(ResultSet::getByte);
					} else if (Byte.class == type) {
						meta.setGetter((rs, col) -> {
							BigDecimal val = rs.getBigDecimal(col);
							if (val == null) {
								return null;
							}
							return val.byteValue();
						});
					} else if (short.class == type) {
						meta.setGetter(ResultSet::getShort);
					} else if (Short.class == type) {
						meta.setGetter((rs, col) -> {
							BigDecimal val = rs.getBigDecimal(col);
							if (val == null) {
								return null;
							}
							return val.shortValue();
						});
					} else if (char.class == type) {
						meta.setGetter((rs, col) -> {
							String val = rs.getString(col);
							return val == null || val.isEmpty() ? '\0' : val.charAt(0);
						});
					} else if (Character.class == type) {
						meta.setGetter((rs, col) -> {
							String val = rs.getString(col);
							return val == null || val.isEmpty() ? null : val.charAt(0);
						});
					} else if (type.isEnum()) {
						meta.setGetter((rs, col) -> {
							String val = rs.getString(col);
							if (val == null) {
								return null;
							}
							Class<Enum> t = (Class<Enum>) type;
							return Enum.valueOf(t, val);
						});
					} else if (java.util.Date.class == type || java.sql.Date.class == type) {
						meta.setGetter(ResultSet::getDate);
					} else if (java.sql.Timestamp.class == type) {
						meta.setGetter(ResultSet::getTimestamp);
					} else if (java.sql.Time.class == type) {
						meta.setGetter(ResultSet::getTime);
					} else if (BigDecimal.class == type) {
						meta.setGetter(ResultSet::getBigDecimal);
					} else {
						meta.setGetter((rs, col) -> {
							try {
								return rs.getObject(col, type);
							} catch (Throwable e) {
								Object val = rs.getObject(col);
								if (val == null || type.isAssignableFrom(val.getClass())) {
									return val;
								}
								return Converters.convertQuietly(type, val);
							}
						});
					}
				}
			}
		}

		metadata.setColumns(columnNames);
		metadata.setFields(fields);
		return metadata;
	}

	private static String castFieldToColumn(String name) {
		int length = name.length();
		StringBuilder sb = new StringBuilder(length + 8);
		for (int i = 0; i < length; i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append('_').append(c);
			} else {
				sb.append(Character.toUpperCase(c));
			}
		}
		return sb.toString();
	}

	private static Set<Field> getDeclaredFields(Object obj) {
		Set<Field> fields = new LinkedHashSet<>();
		for (Class<?> superClass = obj.getClass();
				 superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
			Field[] declaredFields = superClass.getDeclaredFields();
			for (Field field : declaredFields) {
				if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())
					|| Modifier.isTransient(field.getModifiers())) {
					continue;
				}
				fields.add(field);
			}
		}
		return fields;
	}


}

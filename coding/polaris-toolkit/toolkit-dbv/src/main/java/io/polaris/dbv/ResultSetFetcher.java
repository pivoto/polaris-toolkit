package io.polaris.dbv;

import io.polaris.dbv.annotation.ColumnHandler;
import io.polaris.dbv.annotation.ColumnName;
import io.polaris.dbv.handler.ColumnTypeHandler;
import io.polaris.dbv.toolkit.MapKit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Qt
 */
@Slf4j
public class ResultSetFetcher {
	public static Map<String, Object> fetch(final ResultSet rs) throws SQLException {
		final ResultSetMetaData meta = rs.getMetaData();
		final int cnt = meta.getColumnCount();
		final Map<String, Object> map = MapKit.<String, Object>newCaseInsensitiveLinkedHashMap();
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
		ColumnFieldsMeta columnFields = getColumnFieldsMetadata(rs, obj.getClass());
		return fetch(columnFields, rs, obj);
	}

	public static <T> List<T> fetchList(final ResultSet rs, final Class<T> clazz)
		throws SQLException, ReflectiveOperationException {
		ColumnFieldsMeta columnFields = getColumnFieldsMetadata(rs, clazz);
		final List<T> list = new ArrayList<T>();
		while (rs.next()) {
			final T object = clazz.newInstance();
			fetch(columnFields, rs, object);
			list.add(object);
		}
		return list;
	}

	public static Object fetch(ColumnFieldsMeta columnFields, ResultSet rs, Object obj) {
		Set<ColumnFieldMeta> fields = columnFields.getFields();
		for (ColumnFieldMeta meta : fields) {
			Field field = meta.getField();
			ColumnTypeHandler handler = meta.getHandler();
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

	public static ColumnFieldsMeta getColumnFieldsMetadata(ResultSet resultSet, Class clazz) throws SQLException {
		ColumnFieldsMeta metadata = new ColumnFieldsMeta();
		Set<String> columnNames = getColumnNames(resultSet);

		Set<ColumnFieldMeta> fields = new LinkedHashSet<>();

		for (Class<?> superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
			Field[] declaredFields = superClass.getDeclaredFields();
			for (Field field : declaredFields) {
				if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())
					|| Modifier.isTransient(field.getModifiers())) {
					continue;
				}
				String columnName;
				final ColumnName columnNameAnno = field.getAnnotation(ColumnName.class);
				if (columnNameAnno == null) {
					columnName = castFieldToColumn(field.getName());
				} else {
					columnName = columnNameAnno.value().toUpperCase();
				}
				if (!columnNames.contains(columnName)) {
					continue;
				}
				ColumnFieldMeta meta = new ColumnFieldMeta();
				meta.setColumn(columnName);
				meta.setField(field);
				fields.add(meta);
				final ColumnHandler columnHandler = field.getAnnotation(ColumnHandler.class);
				if (columnHandler != null) {
					try {
						ColumnTypeHandler columnTypeHandler = columnHandler.value().newInstance();
						meta.setHandler(columnTypeHandler);
					} catch (Exception e) {
					}
				} else {
					final Class<?> type = field.getType();
					if (type.isPrimitive()) {
						if (int.class == type || Integer.class == type) {
							meta.setHandler((rs, col) -> rs.getInt(col));
						} else if (long.class == type || Long.class == type) {
							meta.setHandler((rs, col) -> rs.getLong(col));
						} else if (boolean.class == type || Boolean.class == type) {
							meta.setHandler((rs, col) -> rs.getBoolean(col));
						} else if (double.class == type || Double.class == type) {
							meta.setHandler((rs, col) -> rs.getDouble(col));
						} else if (float.class == type || Float.class == type) {
							meta.setHandler((rs, col) -> rs.getFloat(col));
						} else if (byte.class == type || Byte.class == type) {
							meta.setHandler((rs, col) -> rs.getByte(col));
						} else if (short.class == type || Short.class == type) {
							meta.setHandler((rs, col) -> rs.getShort(col));
						} else if (char.class == type || Character.class == type) {
							meta.setHandler((rs, col) -> rs.getString(col).length() == 0 ? '\0' : rs.getString(col).charAt(0));
						}
					} else if (type.isEnum()) {
						meta.setHandler((rs, col) -> {
							String val = rs.getString(col);
							if (val == null) return null;
							Class<Enum> t = (Class<Enum>) type;
							return Enum.valueOf(t, val);
						});
					} else {
						meta.setHandler((rs, col) -> {
							try {
								Object val = rs.getObject(col, type);
								return val;
							} catch (SQLException e) {
								return null;
							}
						});
					}
					/*
					if (String.class == type) {
						fields.put(field, (rs, col) -> rs.getString(col));
					} else if (int.class == type || Integer.class == type) {
						fields.put(field, (rs, col) -> rs.getInt(col));
					} else if (long.class == type || Long.class == type) {
						fields.put(field, (rs, col) -> rs.getLong(col));
					} else if (boolean.class == type || Boolean.class == type) {
						fields.put(field, (rs, col) -> rs.getBoolean(col));
					} else if (double.class == type || Double.class == type) {
						fields.put(field, (rs, col) -> rs.getDouble(col));
					} else if (float.class == type || Float.class == type) {
						fields.put(field, (rs, col) -> rs.getFloat(col));
					} else if (byte.class == type || Byte.class == type) {
						fields.put(field, (rs, col) -> rs.getByte(col));
					} else if (short.class == type || Short.class == type) {
						fields.put(field, (rs, col) -> rs.getShort(col));
					} else if (char.class == type || Character.class == type) {
						fields.put(field, (rs, col) -> rs.getString(col).length() == 0 ? '\0' : rs.getString(col).charAt(0));
					} else if (java.util.Date.class == type || java.sql.Date.class == type) {
						fields.put(field, (rs, col) -> rs.getDate(col));
					} else if (java.sql.Timestamp.class == type) {
						fields.put(field, (rs, col) -> rs.getTimestamp(col));
					} else if (java.sql.Time.class == type) {
						fields.put(field, (rs, col) -> rs.getTime(col));
					} else if (java.math.BigDecimal.class == type) {
						fields.put(field, (rs, col) -> rs.getBigDecimal(col));
					} else {
						fields.put(field, (rs, col) -> {
							Object val = rs.getObject(col);
							if (val == null || type.isAssignableFrom(val.getClass())) {
								return val;
							}
							return null;
						});
					}*/
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


	@Data
	public static class ColumnFieldsMeta {
		private Set<String> columns;
		private Set<ColumnFieldMeta> fields;
	}

	@Data
	public static class ColumnFieldMeta {
		private Field field;
		private ColumnTypeHandler handler;
		private String column;
	}
}
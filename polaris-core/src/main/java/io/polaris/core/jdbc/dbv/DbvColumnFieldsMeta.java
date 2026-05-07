package io.polaris.core.jdbc.dbv;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.dbv.annotation.DbvColumn;
import io.polaris.core.map.Maps;
import io.polaris.core.reflect.Reflects;
import lombok.Data;

/**
 * @author Qt
 * @since May 06, 2024
 */
@Data
public class DbvColumnFieldsMeta {
	private Set<String> columns;
	private Set<DbvColumnFieldMeta> fields;

	private static final Map<Class<?>, Set<DbvColumnFieldMeta>> cache = Maps.newSoftMap(new ConcurrentHashMap<>());

	DbvColumnFieldsMeta(Set<String> columns, Set<DbvColumnFieldMeta> fields) {
		this.columns = columns;
		this.fields = fields;
	}

	@Nonnull
	public static DbvColumnFieldsMeta of(ResultSet resultSet, Class<?> clazz) throws SQLException {
		Set<String> columnNames = DbvResultSetFetcher.getColumnNames(resultSet);
		Set<DbvColumnFieldMeta> fields = new LinkedHashSet<>();
		Set<DbvColumnFieldMeta> fullFields = fields(clazz);
		for (DbvColumnFieldMeta meta : fullFields) {
			if (columnNames.contains(meta.getColumn())) {
				fields.add(meta);
			}
		}
		return new DbvColumnFieldsMeta(columnNames, fields);
	}


	private static Set<DbvColumnFieldMeta> fields(Class<?> clazz) {
		Set<DbvColumnFieldMeta> rs = null;
		// 防止因对象回收后导致SoftMap结果丢失，尝试多次获取
		//noinspection StatementWithEmptyBody,ConstantValue
		while ((rs = cache.computeIfAbsent(clazz, DbvColumnFieldsMeta::buildFields)) == null) {
		}
		return rs;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Set<DbvColumnFieldMeta> buildFields(Class<?> clazz) {
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
		return fields;
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

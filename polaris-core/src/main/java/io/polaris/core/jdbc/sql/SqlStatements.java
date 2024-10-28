package io.polaris.core.jdbc.sql;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nonnull;

import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.ExpressionMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.VarNameGenerator;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.query.Queries;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.ConfigurableColumnPredicate;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.string.Strings;

/**
 * 用于Mybatis扩展生成Sql
 *
 * @author Qt
 * @since Jan 27, 2024
 */
@SuppressWarnings("ALL")
public class SqlStatements {
	private static ILogger log = ILoggers.of(SqlStatements.class);

	private static final String KEY_WHERE_PREFIX = "_w";
	private static final String KEY_VALUE_PREFIX = "_v";

	private static VarNameGenerator newWhereVarNameGenerator() {
		return VarNameGenerator.newInstance(KEY_WHERE_PREFIX);
	}

	private static VarNameGenerator newValueVarNameGenerator() {
		return VarNameGenerator.newInstance(KEY_VALUE_PREFIX);
	}

	public static String buildInsert(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY;

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildInsert(bindings, entityClass, entityKey, columnPredicate);
	}

	public static String buildInsert(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, ColumnPredicate columnPredicate) {
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, Collections.emptyMap());

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.insert(tableMeta.getTable());

		VarNameGenerator valueKeyGen = newValueVarNameGenerator();
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean insertable = meta.isInsertable() || meta.isCreateTime() || meta.isUpdateTime();
			if (!insertable) {
				continue;
			}
			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}
			Object val1 = entityMap.get(meta.getFieldName());
			Object val = BindingValues.getValueForInsert(meta, val1);
			if (meta.isVersion()) {
				val = val == null ? 1L : ((Number) val).longValue();
			}
			if (Objs.isNotEmpty(val)) {
				String keyName = valueKeyGen.generate();
				sql.columnAndValue(columnName, "#{" + keyName + "}");
				bindings.put(keyName, val);
			} else {
				// 需要包含空值字段
				if (columnPredicate.isIncludedEmptyColumn(name)) {
					sql.columnAndValue(columnName, "NULL");
				}
			}
		}
		return sql.toSqlString();
	}


	public static String buildDeleteById(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		return buildDeleteById(bindings, entityClass, entityKey, whereKey);
	}

	public static String buildDeleteById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey) {
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.delete(tableMeta.getTable());

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity == null) {
			entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		}
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));

		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();

			Object val = entityMap.get(name);
			// 按主键条件删除，无需判断列的包含条件
			if (primaryKey || version) {
				if (Objs.isNotEmpty(val)) {
					appendSqlWhereWithVal(bindings, sql, meta, val, whereKeyGen.generate());
				} else {
					sql.where(columnName + " IS NULL");
				}
			}
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	public static String buildDeleteByAny(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		/* String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY; */
		// 兼容 where keys
		String[] includeColumnsKey = {BindingKeys.WHERE_INCLUDE_COLUMNS, BindingKeys.INCLUDE_COLUMNS};
		String[] excludeColumnsKey = {BindingKeys.WHERE_EXCLUDE_COLUMNS, BindingKeys.EXCLUDE_COLUMNS};
		String[] includeEmptyColumnsKey = {BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS, BindingKeys.INCLUDE_EMPTY_COLUMNS};
		String[] includeAllEmptyKey = {BindingKeys.WHERE_INCLUDE_EMPTY, BindingKeys.INCLUDE_EMPTY};
		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildDeleteByAny(bindings, entityClass, entityKey, whereKey, columnPredicate);
	}

	public static String buildDeleteByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate) {
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.delete(tableMeta.getTable());

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate);
			}
		}
		entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate);
			}
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	public static String buildUpdateById(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY;

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildUpdateById(bindings, entityClass, entityKey, whereKey, columnPredicate);
	}

	public static String buildUpdateById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity == null) {
			entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		}
		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		VarNameGenerator valueKeyGen = newValueVarNameGenerator();
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();
			Object val = entityMap.get(name);
			boolean updatable = meta.isUpdatable() || meta.isVersion() || meta.isUpdateTime();
			if (primaryKey || version) {
				if (Objs.isEmpty(val)) {
					sql.where(columnName + " IS NULL");
				} else {
					String keyName = whereKeyGen.generate();
					sql.where(columnName + " = #{" + keyName + "}");
					bindings.put(keyName, val);
				}
			}
			if (!updatable) {
				continue;
			}
			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}

			if (Objs.isEmpty(val)) {
				if (meta.isUpdateTime()) {
					Object value = new Date();
					val = Converters.convertQuietly(meta.getFieldType(), value);
				}
			}
			if (Objs.isEmpty(val)) {
				String updateDefault = meta.getUpdateDefault();
				if (Strings.isNotBlank(updateDefault)) {
					val = Converters.convertQuietly(meta.getFieldType(), updateDefault);
				}
			}
			if (version) {
				val = Objs.isEmpty(val) ? 1L : ((Number) val).longValue() + 1;
			} else if (primaryKey) {
				// 不更新主键值
				continue;
			}
			if (Objs.isNotEmpty(val)) {
				String keyName = valueKeyGen.generate();
				sql.set(columnName + " = #{" + keyName + "}");
				bindings.put(keyName, val);
			} else {
				// 需要包含空值字段
				boolean include = columnPredicate.isIncludedEmptyColumn(name);
				if (include) {
					sql.set(columnName + " = NULL");
				}
			}
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	public static String buildLogicDeleteById(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		return buildLogicDeleteById(bindings, entityClass, entityKey, whereKey);
	}

	public static String buildLogicDeleteById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		if (!tableMeta.getColumns().values().stream().anyMatch(c -> c.isLogicDeleted())) {
			log.warn("实体{}不存在逻辑删除字段！", entityClass);
			return buildDeleteById(bindings, entityClass, entityKey, whereKey);
		}

		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity == null) {
			entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		}
		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		VarNameGenerator valueKeyGen = newValueVarNameGenerator();
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));

		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();
			boolean logicDeleted = meta.isLogicDeleted();
			if (primaryKey || version) {
				Object val = entityMap.get(name);
				if (Objs.isEmpty(val)) {
					sql.where(columnName + " IS NULL");
				} else {
					String keyName = whereKeyGen.generate();
					sql.where(columnName + " = #{" + keyName + "}");
					bindings.put(keyName, val);
				}
			}
			Object val = null;
			// 只更新逻辑删除、更新时间、版本等字段
			if (logicDeleted) {
				val = Converters.convertQuietly(meta.getFieldType(), true);
			} else if (meta.isUpdateTime()) {
				val = entityMap.get(name);
				if (Objs.isEmpty(val)) {
					Object value = new Date();
					val = Converters.convertQuietly(meta.getFieldType(), value);
				}
			} else if (version) {
				val = entityMap.get(name);
				val = Objs.isEmpty(val) ? 1L : ((Number) val).longValue() + 1;
			}
			if (Objs.isNotEmpty(val)) {
				String keyName = valueKeyGen.generate();
				sql.set(columnName + " = #{" + keyName + "}");
				bindings.put(keyName, val);
			}
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	public static String buildUpdateByAny(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY;
		String whereIncludeColumnsKey = BindingKeys.WHERE_INCLUDE_COLUMNS;
		String whereExcludeColumnsKey = BindingKeys.WHERE_EXCLUDE_COLUMNS;
		String whereIncludeEmptyColumnsKey = BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS;
		String whereIncludeAllEmptyKey = BindingKeys.WHERE_INCLUDE_EMPTY;

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		ColumnPredicate whereColumnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, whereIncludeColumnsKey, null, whereExcludeColumnsKey,
			null, whereIncludeEmptyColumnsKey, false, whereIncludeAllEmptyKey);

		return buildUpdateByAny(bindings, entityClass, entityKey, whereKey, columnPredicate, whereColumnPredicate);
	}

	public static String buildUpdateByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate, ColumnPredicate whereColumnPredicate) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		VarNameGenerator valueKeyGen = newValueVarNameGenerator();
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, Collections.emptyMap());
		Object where = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();

			boolean updatable = meta.isUpdatable() || meta.isVersion() || meta.isUpdateTime();
			if (!updatable) {
				continue;
			}
			if (primaryKey) {
				// 不更新主键值
				continue;
			}
			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}
			Object val = entityMap.get(meta.getFieldName());
			Object entityVal = BindingValues.getValueForUpdate(meta, val);
			if (version) {
				entityVal = Objs.isEmpty(entityVal) ? 1L : ((Number) entityVal).longValue() + 1;
			}
			if (Objs.isNotEmpty(entityVal)) {
				String keyName = valueKeyGen.generate();
				sql.set(columnName + " = #{" + keyName + "}");
				bindings.put(keyName, entityVal);
			} else {
				// 需要包含空值字段
				boolean include = columnPredicate.isIncludedEmptyColumn(name);
				if (include) {
					sql.set(columnName + " = NULL");
				}
			}
		}
		// where 条件
		if (where instanceof Criteria) {
			appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, where);
		} else if (where != null) {
			appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, where, whereColumnPredicate);
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	public static String buildLogicDeleteByAny(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String whereIncludeColumnsKey = BindingKeys.WHERE_INCLUDE_COLUMNS;
		String whereExcludeColumnsKey = BindingKeys.WHERE_EXCLUDE_COLUMNS;
		String whereIncludeEmptyColumnsKey = BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS;
		String whereIncludeAllEmptyKey = BindingKeys.WHERE_INCLUDE_EMPTY;
		ColumnPredicate whereColumnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, whereIncludeColumnsKey, null, whereExcludeColumnsKey,
			null, whereIncludeEmptyColumnsKey, false, whereIncludeAllEmptyKey);

		return buildLogicDeleteByAny(bindings, entityClass, entityKey, whereKey, whereColumnPredicate);
	}

	public static String buildLogicDeleteByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate whereColumnPredicate) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		if (!tableMeta.getColumns().values().stream().anyMatch(c -> c.isLogicDeleted())) {
			log.warn("实体{}不存在逻辑删除字段！", entityClass);
			return buildDeleteByAny(bindings, entityClass, entityKey, whereKey,whereColumnPredicate);
		}

		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		VarNameGenerator valueKeyGen = newValueVarNameGenerator();
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, Collections.emptyMap());
		Object where = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();
			boolean logicDeleted = meta.isLogicDeleted();

			Object val = null;
			// 只更新逻辑删除、更新时间、版本等字段
			if (logicDeleted) {
				val = Converters.convertQuietly(meta.getFieldType(), true);
			} else if (meta.isUpdateTime()) {
				val = entityMap.get(name);
				if (Objs.isEmpty(val)) {
					Object value = new Date();
					val = Converters.convertQuietly(meta.getFieldType(), value);
				}
			} else if (version) {
				val = entityMap.get(name);
				val = Objs.isEmpty(val) ? 1L : ((Number) val).longValue() + 1;
			}
			if (Objs.isNotEmpty(val)) {
				String keyName = valueKeyGen.generate();
				sql.set(columnName + " = #{" + keyName + "}");
				bindings.put(keyName, val);
			}
		}
		// where 条件
		if (where instanceof Criteria) {
			appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, where);
		} else if (where != null) {
			appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, where, whereColumnPredicate);
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	public static String buildCount(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		/* String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY; */
		// 兼容 where keys
		String[] includeColumnsKey = {BindingKeys.WHERE_INCLUDE_COLUMNS, BindingKeys.INCLUDE_COLUMNS};
		String[] excludeColumnsKey = {BindingKeys.WHERE_EXCLUDE_COLUMNS, BindingKeys.EXCLUDE_COLUMNS};
		String[] includeEmptyColumnsKey = {BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS, BindingKeys.INCLUDE_EMPTY_COLUMNS};
		String[] includeAllEmptyKey = {BindingKeys.WHERE_INCLUDE_EMPTY, BindingKeys.INCLUDE_EMPTY};

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey,
			null, excludeColumnsKey,
			null, includeEmptyColumnsKey,
			false, includeAllEmptyKey);
		return buildCount(bindings, entityClass, entityKey, whereKey, columnPredicate);
	}

	public static String buildCount(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate) {
		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());
		sql.select("COUNT(*)");

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate);
			}
		}
		entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate);
			}
		}
		return sql.toSqlString();
	}


	public static String buildExistsById(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		return buildExistsById(bindings, entityClass, entityKey, whereKey);
	}

	public static String buildExistsById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());
		sql.select("COUNT(*) EXISTED");

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity == null) {
			entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		}
		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));

		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();
			Object val = entityMap.get(name);
			if (primaryKey) {
				if (val == null) {
					sql.where(columnName + " IS NULL");
				} else {
					String keyName = whereKeyGen.generate();
					sql.where(columnName + " = #{" + keyName + "}");
					bindings.put(keyName, val);
				}
			}
		}
		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	public static String buildExistsByAny(Map<String, Object> bindings, Class<?> entityClass) {
		return buildExistsByAny(bindings, entityClass, false);
	}

	public static String buildExistsByAny(Map<String, Object> bindings, Class<?> entityClass, boolean queryByCount) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		// 兼容 where keys
		String[] includeColumnsKey = {BindingKeys.WHERE_INCLUDE_COLUMNS, BindingKeys.INCLUDE_COLUMNS};
		String[] excludeColumnsKey = {BindingKeys.WHERE_EXCLUDE_COLUMNS, BindingKeys.EXCLUDE_COLUMNS};
		String[] includeEmptyColumnsKey = {BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS, BindingKeys.INCLUDE_EMPTY_COLUMNS};
		String[] includeAllEmptyKey = {BindingKeys.WHERE_INCLUDE_EMPTY, BindingKeys.INCLUDE_EMPTY};

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildExistsByAny(bindings, entityClass, entityKey, whereKey, columnPredicate, queryByCount);
	}


	public static String buildExistsByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate) {
		return buildExistsByAny(bindings, entityClass, entityKey, whereKey, columnPredicate, false);
	}


	public static String buildExistsByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate, boolean queryByCount) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());
		if (queryByCount) {
			sql.select("COUNT(*) EXISTED");
		} else {
			sql.select("1 EXISTED");
		}

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate);
			}
		}
		entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate);
			}
		}
		return sql.toSqlString();
	}

	public static String buildSelectById(Map<String, Object> bindings, Class<?> entityClass) {
		return buildSelectById(bindings, entityClass, false);
	}

	public static String buildSelectById(Map<String, Object> bindings, Class<?> entityClass, boolean exceptLogicDeleted) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String orderByKey = BindingKeys.ORDER_BY;
		return buildSelectById(bindings, entityClass, entityKey, whereKey, orderByKey, exceptLogicDeleted);
	}

	public static String buildSelectById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, String orderByKey) {
		return buildSelectById(bindings, entityClass, entityKey, whereKey, orderByKey, false);
	}

	public static String buildSelectById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, String orderByKey, boolean exceptLogicDeleted) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity == null) {
			entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		}
		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));

		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			//boolean version = meta.isVersion();
			Object val = entityMap.get(name);

			sql.select(columnName + " " + name);

			if (primaryKey) {
				if (val == null) {
					sql.where(columnName + " IS NULL");
				} else {
					String keyName = whereKeyGen.generate();
					sql.where(columnName + " = #{" + keyName + "}");
					bindings.put(keyName, val);
				}
			} else if (exceptLogicDeleted && meta.isLogicDeleted()) {
				// 强制添加非逻辑删除条件
				String keyName = whereKeyGen.generate();
				val = Converters.convertQuietly(meta.getFieldType(), false);
				sql.where(columnName + " = #{" + keyName + "}");
				bindings.put(keyName, val);
			}
		}
		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	public static String buildSelectByAny(Map<String, Object> bindings, Class<?> entityClass) {
		return buildSelectByAny(bindings, entityClass, false);
	}

	public static String buildSelectByAny(Map<String, Object> bindings, Class<?> entityClass, boolean exceptLogicDeleted) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String orderByKey = BindingKeys.ORDER_BY;
		// 兼容 where keys
		String[] includeColumnsKey = {BindingKeys.WHERE_INCLUDE_COLUMNS, BindingKeys.INCLUDE_COLUMNS};
		String[] excludeColumnsKey = {BindingKeys.WHERE_EXCLUDE_COLUMNS, BindingKeys.EXCLUDE_COLUMNS};
		String[] includeEmptyColumnsKey = {BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS, BindingKeys.INCLUDE_EMPTY_COLUMNS};
		String[] includeAllEmptyKey = {BindingKeys.WHERE_INCLUDE_EMPTY, BindingKeys.INCLUDE_EMPTY};

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildSelectByAny(bindings, entityClass, entityKey, whereKey, orderByKey, columnPredicate);
	}


	public static String buildSelectByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, String orderByKey, ColumnPredicate columnPredicate) {
		return buildSelectByAny(bindings, entityClass, entityKey, whereKey, orderByKey, columnPredicate, false);
	}


	public static String buildSelectByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, String orderByKey, ColumnPredicate columnPredicate,
		boolean exceptLogicDeleted) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());


		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();

		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			sql.select(columnName + " " + name);
		}
		for (Map.Entry<String, ExpressionMeta> entry : tableMeta.getExpressions().entrySet()) {
			String name = entry.getKey();
			ExpressionMeta meta = entry.getValue();
			if (meta.isSelectable()) {
				String columnName = meta.getExpressionWithoutTableAlias();
				sql.select(columnName + " " + name);
			}
		}

		if (exceptLogicDeleted) {
			// 强制添加非逻辑删除条件
			tableMeta.getColumns().values().stream()
				.filter(c -> c.isLogicDeleted())
				.forEach(meta -> {
					String columnName = meta.getColumnName();
					Object val = Converters.convertQuietly(meta.getFieldType(), false);
					String keyName = whereKeyGen.generate();
					sql.where(columnName + " = #{" + keyName + "}");
					bindings.put(keyName, val);
				});
		}

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate);
			}
		}
		entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate);
			}
		}
		// 排序字段
		Object orderByObj = BindingValues.getBindingValueOrDefault(bindings, orderByKey, null);
		OrderBy orderBy = null;
		if (orderByObj instanceof String) {
			orderBy = Queries.newOrderBy((String) orderByObj);
		} else if (orderByObj instanceof OrderBy) {
			orderBy = (OrderBy) orderByObj;
		}
		if (orderBy != null) {
			for (OrderBy.Item item : orderBy.getItems()) {
				ColumnMeta columnMeta = tableMeta.getColumns().get(item.getField());
				if (columnMeta != null) {
					sql.orderBy(columnMeta.getColumnName() + " " + item.getDirection().getSqlText());
					continue;
				}

				ExpressionMeta expressionMeta = tableMeta.getExpressions().get(item.getField());
				if (expressionMeta != null) {
					sql.orderBy(expressionMeta.getExpressionWithoutTableAlias() + " " + item.getDirection().getSqlText());
					continue;
				}
			}
		}

		return sql.toSqlString();
	}

	private static void appendSqlWhereWithEntity(Map<String, Object> bindings, Class<?> entityClass
		, TableMeta tableMeta, SqlStatement sql, VarNameGenerator whereKeyGen
		, Object entity, ColumnPredicate columnPredicate) {
		// 实体查询条件
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();

			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}

			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			Object val = entityMap.get(name);

			if (Objs.isNotEmpty(val)) {
				appendSqlWhereWithVal(bindings, sql, meta, val, whereKeyGen.generate());
			} else {
				// 需要包含空值字段
				boolean include = columnPredicate.isIncludedEmptyColumn(name);
				if (include) {
					sql.where(columnName + " IS NULL");
				}
			}
		}
		for (Map.Entry<String, ExpressionMeta> entry : tableMeta.getExpressions().entrySet()) {
			String name = entry.getKey();

			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}

			ExpressionMeta meta = entry.getValue();
			String columnName = Strings.isNotBlank(meta.getTableAliasPlaceholder()) ?
				meta.getExpression().replace(meta.getTableAliasPlaceholder(), "") : meta.getExpression();
			Object val = entityMap.get(name);

			if (Objs.isNotEmpty(val)) {
				appendSqlWhereWithVal(bindings, sql, meta, val, whereKeyGen.generate());
			} else {
				// 需要包含空值字段
				boolean include = columnPredicate.isIncludedEmptyColumn(name);
				if (include) {
					sql.where(columnName + " IS NULL");
				}
			}
		}
	}

	private static void appendSqlWhereWithCriteria(Map<String, Object> bindings
		, TableMeta tableMeta, SqlStatement sql, VarNameGenerator whereKeyGen, Object criteria) {
		// 追加查询条件
		if (criteria instanceof Criteria) {
			Function<String, String> columnDiscovery = Queries.newColumnDiscovery(tableMeta);
			SqlNode sqlNode = Queries.parse((Criteria) criteria, false, columnDiscovery);
			if (!sqlNode.isSkipped()) {
				BoundSql boundSql = sqlNode.asBoundSql(whereKeyGen);
				sql.where(boundSql.getText());
				bindings.putAll(boundSql.getBindings());
			}
		}
	}

	/**
	 * 添加查询条件， 支持处理集合与数组类型
	 */
	private static void appendSqlWhereWithVal(@Nonnull Map<String, Object> bindings
		, @Nonnull SqlStatement sql, @Nonnull ExpressionMeta meta, @Nonnull Object val, String key) {
		String columnName = Strings.isNotBlank(meta.getTableAliasPlaceholder()) ? meta.getExpression().replace(meta.getTableAliasPlaceholder(), "") : meta.getExpression();
		Class<?> fieldType = meta.getFieldType();
		appendSqlWhereWithVal(bindings, sql, columnName, fieldType, val, key);
	}

	/**
	 * 添加查询条件， 支持处理集合与数组类型
	 */
	private static void appendSqlWhereWithVal(@Nonnull Map<String, Object> bindings
		, @Nonnull SqlStatement sql, @Nonnull ColumnMeta meta, @Nonnull Object val, String key) {
		String columnName = meta.getColumnName();
		Class<?> fieldType = meta.getFieldType();
		appendSqlWhereWithVal(bindings, sql, columnName, fieldType, val, key);
	}

	private static void appendSqlWhereWithVal(Map<String, Object> bindings, SqlStatement sql, String columnName, Class<?> fieldType, Object val, String key) {
		// 日期字段
		if (Date.class.isAssignableFrom(fieldType)) {
			// 两个元素的日期类字段特殊处理，认为是日期范围条件
			Date[] range = BindingValues.getDateRangeOrNull(val);
			if (range != null) {
				Date start = range[0];
				Date end = range[1];
				if (start != null) {
					sql.where(columnName + " >= #{" + key + "0} ");
					bindings.put(key + "0", start);
				}
				if (end != null) {
					sql.where(columnName + " <= #{" + key + "1} ");
					bindings.put(key + "1", end);
				}
				// 日期条件完毕
				return;
			}
		}

		// 文本字段
		if (String.class.isAssignableFrom(fieldType)) {
			if (val instanceof String && (((String) val).startsWith("%") || ((String) val).endsWith("%"))) {
				bindings.put(key, val);
				sql.where(columnName + " like #{" + key + "} ");
				return;
			}
		}

		if (val instanceof Iterable) {
			StringBuilder where = new StringBuilder();
			where.append(columnName).append(" IN ( ");
			int i = 0;
			boolean first = true;
			Iterator<?> iter = ((Iterable<?>) val).iterator();
			while (iter.hasNext()) {
				Object next = iter.next();
				if (i > 0 && i % 1000 == 0) {
					where.append(" ) AND ").append(columnName).append(" IN ( ");
					first = true;
				}
				if (first) {
					first = false;
				} else {
					where.append(", ");
				}
				where.append("#{").append(key).append(i).append("}");
				bindings.put(key + i, Converters.convertQuietly(fieldType, next));
				i++;
			}
			where.append(" ) ");
			sql.where(where.toString());
		} else if (val.getClass().isArray()) {
			int len = Array.getLength(val);
			StringBuilder where = new StringBuilder();
			where.append(columnName).append(" IN ( ");
			boolean first = true;
			for (int i = 0; i < len; i++) {
				Object next = Array.get(val, i);
				if (i > 0 && i % 1000 == 0) {
					where.append(" ) AND ").append(columnName).append(" IN ( ");
					first = true;
				}
				if (first) {
					first = false;
				} else {
					where.append(", ");
				}
				where.append("#{").append(key).append(i).append("}");
				bindings.put(key + i, Converters.convertQuietly(fieldType, next));
			}
			where.append(" ) ");
			sql.where(where.toString());
		} else {
			sql.where(columnName + " = #{" + key + "} ");
			bindings.put(key, Converters.convertQuietly(fieldType, val));
		}
	}


}

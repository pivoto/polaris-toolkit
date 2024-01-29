package io.polaris.core.jdbc.sql;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.VarNameGenerator;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.query.Queries;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8,  Jan 27, 2024
 */
@SuppressWarnings("ALL")
public class SqlStatements {

	static final String KEY_WHERE_PREFIX = "w_";
	static final String KEY_VALUE_PREFIX = "v_";

	public static String buildInsert(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY;

		return buildInsert(bindings, entityClass, entityKey,
			null, includeColumnsKey,
			null, excludeColumnsKey,
			null, includeEmptyColumnsKey,
			false, includeAllEmptyKey
		);
	}

	public static String buildInsert(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey,
		String[] includeColumns, String includeColumnsKey,
		String[] excludeColumns, String excludeColumnsKey,
		String[] includeEmptyColumns, String includeEmptyColumnsKey,
		boolean includeAllEmpty, String includeAllEmptyKey
	) {

		Predicate<String> isIncludeColumns = EntityStatements.getColumnPredicate(bindings, includeColumns, includeColumnsKey);
		Predicate<String> isExcludeColumns = EntityStatements.getColumnPredicate(bindings, excludeColumns, excludeColumnsKey);
		Predicate<String> isIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		includeAllEmpty = EntityStatements.isIncludeEmpty(bindings, includeAllEmpty, includeAllEmptyKey);
		Object entity = EntityStatements.getObjectOfKey(bindings, entityKey, Collections.emptyMap());


		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.insert(tableMeta.getTable());

		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean insertable = meta.isInsertable() || meta.isCreateTime() || meta.isUpdateTime();
			if (!insertable) {
				continue;
			}
			// 不在包含列表
			if (isIncludeColumns != null && !isIncludeColumns.test(name)) {
				continue;
			}
			// 在排除列表
			if (isExcludeColumns != null && isExcludeColumns.test(name)) {
				continue;
			}
			Object val = EntityStatements.getValForInsert(entityMap, meta);
			if (meta.isVersion()) {
				val = val == null ? 1L : ((Number) val).longValue() + 1;
			}
			if (Objs.isNotEmpty(val)) {
				sql.columnAndValue(columnName, "#{" + KEY_VALUE_PREFIX + name + "}");
				bindings.put(KEY_VALUE_PREFIX + name, val);
			} else {
				// 需要包含空值字段
				if (includeAllEmpty || isIncludeEmptyColumns != null && isIncludeEmptyColumns.test(name)) {
					sql.columnAndValue(columnName, "NULL");
				}
			}
		}
		return sql.toSqlString();
	}


	public static String buildDelete(Map<String, Object> bindings, Class<?> entityClass, boolean byId
	) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY;

		return SqlStatements.buildDelete(bindings, entityClass, byId,
			entityKey, whereKey,
			null, includeColumnsKey,
			null, excludeColumnsKey,
			null, includeEmptyColumnsKey,
			false, includeAllEmptyKey
		);
	}

	public static String buildDelete(Map<String, Object> bindings, Class<?> entityClass,
		boolean byId, String entityKey, String whereKey,
		String[] includeColumns, String includeColumnsKey,
		String[] excludeColumns, String excludeColumnsKey,
		String[] includeEmptyColumns, String includeEmptyColumnsKey,
		boolean includeAllEmpty, String includeAllEmptyKey
	) {

		Predicate<String> isIncludeColumns = EntityStatements.getColumnPredicate(bindings, includeColumns, includeColumnsKey);
		Predicate<String> isExcludeColumns = EntityStatements.getColumnPredicate(bindings, excludeColumns, excludeColumnsKey);
		Predicate<String> isIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		includeAllEmpty = EntityStatements.isIncludeEmpty(bindings, includeAllEmpty, includeAllEmptyKey);

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.delete(tableMeta.getTable());

		VarNameGenerator whereKeyGen = VarNameGenerator.newInstance(KEY_WHERE_PREFIX);
		if (byId) {
			Object entity = EntityStatements.getObjectOfKey(bindings, entityKey, null);
			if (entity == null) {
				entity = EntityStatements.getObjectOfKey(bindings, whereKey, Collections.emptyMap());
			}
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);

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
		} else {
			Object entity = EntityStatements.getObjectOfKey(bindings, entityKey, null);
			if (entity != null) {
				if (entity instanceof Criteria) {
					appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
				} else {
					appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity,
						isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);
				}
			}
			entity = EntityStatements.getObjectOfKey(bindings, whereKey, null);
			if (entity != null) {
				if (entity instanceof Criteria) {
					appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
				} else {
					appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity,
						isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);
				}
			}
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalStateException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	public static String buildUpdateById(Map<String, Object> bindings, Class<?> entityClass
	) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY;

		return SqlStatements.buildUpdateById(bindings, entityClass,
			entityKey, whereKey,
			null, includeColumnsKey,
			null, excludeColumnsKey,
			null, includeEmptyColumnsKey,
			false, includeAllEmptyKey
		);
	}

	public static String buildUpdateById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey,
		String[] includeColumns, String includeColumnsKey,
		String[] excludeColumns, String excludeColumnsKey,
		String[] includeEmptyColumns, String includeEmptyColumnsKey,
		boolean includeAllEmpty, String includeAllEmptyKey
	) {

		Predicate<String> isIncludeColumns = EntityStatements.getColumnPredicate(bindings, includeColumns, includeColumnsKey);
		Predicate<String> isExcludeColumns = EntityStatements.getColumnPredicate(bindings, excludeColumns, excludeColumnsKey);
		Predicate<String> isIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		includeAllEmpty = EntityStatements.isIncludeEmpty(bindings, includeAllEmpty, includeAllEmptyKey);

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		Object entity = EntityStatements.getObjectOfKey(bindings, entityKey, null);
		if (entity == null) {
			entity = EntityStatements.getObjectOfKey(bindings, whereKey, Collections.emptyMap());
		}
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);

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
					sql.where(columnName + " = #{" + KEY_WHERE_PREFIX + name + "}");
					bindings.put(KEY_WHERE_PREFIX + name, val);
				}
			}
			if (!updatable) {
				continue;
			}
			// 不在包含列表
			if (isIncludeColumns != null && !isIncludeColumns.test(name)) {
				continue;
			}
			// 在排除列表
			if (isExcludeColumns != null && isExcludeColumns.test(name)) {
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
				sql.set(columnName + " = #{" + KEY_VALUE_PREFIX + name + "}");
				bindings.put(KEY_VALUE_PREFIX + name, val);
			} else {
				boolean include =
					// 需要包含空值字段
					includeAllEmpty || (isIncludeEmptyColumns != null && isIncludeEmptyColumns.test(name));
				if (include) {
					sql.set(columnName + " = NULL");
				}
			}
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalStateException("缺少条件子句");
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

		return SqlStatements.buildUpdateByAny(bindings, entityClass,
			entityKey, whereKey,
			null, includeColumnsKey,
			null, excludeColumnsKey,
			null, includeEmptyColumnsKey,
			false, includeAllEmptyKey,
			null, whereIncludeColumnsKey,
			null, whereExcludeColumnsKey,
			null, whereIncludeEmptyColumnsKey,
			false, whereIncludeAllEmptyKey
		);
	}

	public static String buildUpdateByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey,
		String[] includeColumns, String includeColumnsKey,
		String[] excludeColumns, String excludeColumnsKey,
		String[] includeEmptyColumns, String includeEmptyColumnsKey,
		boolean includeAllEmpty, String includeAllEmptyKey,
		String[] whereIncludeColumns, String whereIncludeColumnsKey,
		String[] whereExcludeColumns, String whereExcludeColumnsKey,
		String[] whereIncludeEmptyColumns, String whereIncludeEmptyColumnsKey,
		boolean whereIncludeAllEmpty, String whereIncludeAllEmptyKey
	) {

		Predicate<String> isIncludeColumns = EntityStatements.getColumnPredicate(bindings, includeColumns, includeColumnsKey);
		Predicate<String> isExcludeColumns = EntityStatements.getColumnPredicate(bindings, excludeColumns, excludeColumnsKey);
		Predicate<String> isIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		includeAllEmpty = EntityStatements.isIncludeEmpty(bindings, includeAllEmpty, includeAllEmptyKey);

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		Predicate<String> isWhereIncludeColumns = EntityStatements.getColumnPredicate(bindings, whereIncludeColumns, whereIncludeColumnsKey);
		Predicate<String> isWhereExcludeColumns = EntityStatements.getColumnPredicate(bindings, whereExcludeColumns, whereExcludeColumnsKey);
		Predicate<String> isWhereIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, whereIncludeEmptyColumns, whereIncludeEmptyColumnsKey);
		whereIncludeAllEmpty = EntityStatements.isIncludeEmpty(bindings, whereIncludeAllEmpty, whereIncludeAllEmptyKey);

		VarNameGenerator whereKeyGen = VarNameGenerator.newInstance(KEY_WHERE_PREFIX);
		Object entity = EntityStatements.getObjectOfKey(bindings, entityKey, Collections.emptyMap());
		Object where = EntityStatements.getObjectOfKey(bindings, whereKey, Collections.emptyMap());
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
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
			if (isIncludeColumns != null && !isIncludeColumns.test(name)) {
				continue;
			}
			// 在排除列表
			if (isExcludeColumns != null && isExcludeColumns.test(name)) {
				continue;
			}
			Object entityVal = EntityStatements.getValForUpdate(entityMap, meta);
			if (version) {
				entityVal = Objs.isEmpty(entityVal) ? 1L : ((Number) entityVal).longValue() + 1;
			}
			if (Objs.isNotEmpty(entityVal)) {
				sql.set(columnName + " = #{" + KEY_VALUE_PREFIX + name + "}");
				bindings.put(KEY_VALUE_PREFIX + name, entityVal);
			} else {
				boolean include =
					// 需要包含空值字段
					includeAllEmpty || (isIncludeEmptyColumns != null && isIncludeEmptyColumns.test(name));
				if (include) {
					sql.set(columnName + " = NULL");
				}
			}
		}
		// where 条件
		if (where instanceof Criteria) {
			appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, where);
		} else if (where != null) {
			appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, where
				, isWhereIncludeColumns, isWhereExcludeColumns
				, whereIncludeAllEmpty, isWhereIncludeEmptyColumns);
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalStateException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	public static String buildCount(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY;
		return buildCount(bindings, entityClass,
			entityKey, whereKey,
			null, includeColumnsKey,
			null, excludeColumnsKey,
			null, includeEmptyColumnsKey,
			false, includeAllEmptyKey);
	}

	public static String buildCount(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey,
		String[] includeColumns, String includeColumnsKey,
		String[] excludeColumns, String excludeColumnsKey,
		String[] includeEmptyColumns, String includeEmptyColumnsKey,
		boolean includeAllEmpty, String includeAllEmptyKey) {

		Predicate<String> isIncludeColumns = EntityStatements.getColumnPredicate(bindings, includeColumns, includeColumnsKey);
		Predicate<String> isExcludeColumns = EntityStatements.getColumnPredicate(bindings, excludeColumns, excludeColumnsKey);
		Predicate<String> isIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		includeAllEmpty = EntityStatements.isIncludeEmpty(bindings, includeAllEmpty, includeAllEmptyKey);
		VarNameGenerator whereKeyGen = VarNameGenerator.newInstance(KEY_WHERE_PREFIX);

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());
		sql.select("COUNT(*)");

		Object entity = EntityStatements.getObjectOfKey(bindings, entityKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity,
					isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);
			}
		}
		entity = EntityStatements.getObjectOfKey(bindings, whereKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity,
					isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);
			}
		}
		return sql.toSqlString();
	}

	public static String buildSelect(Map<String, Object> bindings, Class<?> entityClass, boolean byId) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY;
		String orderByKey = BindingKeys.ORDER_BY;
		return buildSelect(bindings, entityClass,
			byId, entityKey, whereKey, orderByKey,
			null, includeColumnsKey,
			null, excludeColumnsKey,
			null, includeEmptyColumnsKey,
			false, includeAllEmptyKey);
	}


	public static String buildSelect(Map<String, Object> bindings, Class<?> entityClass,
		boolean byId, String entityKey, String whereKey, String orderByKey,
		String[] includeColumns, String includeColumnsKey,
		String[] excludeColumns, String excludeColumnsKey,
		String[] includeEmptyColumns, String includeEmptyColumnsKey,
		boolean includeAllEmpty, String includeAllEmptyKey) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());

		if (byId) {
			Object entity = EntityStatements.getObjectOfKey(bindings, entityKey, null);
			if (entity == null) {
				entity = EntityStatements.getObjectOfKey(bindings, whereKey, Collections.emptyMap());
			}
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);

			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				ColumnMeta meta = entry.getValue();
				String columnName = meta.getColumnName();
				boolean primaryKey = meta.isPrimaryKey();
				boolean version = meta.isVersion();
				Object val = entityMap.get(name);

				sql.select(columnName + " " + name);

				if (primaryKey || version) {
					if (val == null) {
						sql.where(columnName + " IS NULL");
					} else {
						sql.where(columnName + " = #{" + KEY_WHERE_PREFIX + name + "}");
						bindings.put(KEY_WHERE_PREFIX + name, val);
					}
				}
			}
			if (!sql.where().hasConditions()) {
				throw new IllegalStateException("缺少条件子句");
			}
		} else {
			Predicate<String> isIncludeColumns = EntityStatements.getColumnPredicate(bindings, includeColumns, includeColumnsKey);
			Predicate<String> isExcludeColumns = EntityStatements.getColumnPredicate(bindings, excludeColumns, excludeColumnsKey);
			Predicate<String> isIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
			includeAllEmpty = EntityStatements.isIncludeEmpty(bindings, includeAllEmpty, includeAllEmptyKey);
			VarNameGenerator whereKeyGen = VarNameGenerator.newInstance(KEY_WHERE_PREFIX);

			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				ColumnMeta meta = entry.getValue();
				String columnName = meta.getColumnName();
				sql.select(columnName + " " + name);
			}

			Object entity = EntityStatements.getObjectOfKey(bindings, entityKey, null);
			if (entity != null) {
				if (entity instanceof Criteria) {
					appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
				} else {
					appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity,
						isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);
				}
			}
			entity = EntityStatements.getObjectOfKey(bindings, whereKey, null);
			if (entity != null) {
				if (entity instanceof Criteria) {
					appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
				} else {
					appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity,
						isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);
				}
			}
			// 排序字段
			Object orderByObj = EntityStatements.getObjectOfKey(bindings, orderByKey, null);
			OrderBy orderBy = null;
			if (orderByObj instanceof String) {
				orderBy = Queries.newOrderBy((String) orderByObj);
			} else if (orderByObj instanceof OrderBy) {
				orderBy = (OrderBy) orderByObj;
			}
			if (orderBy != null) {
				for (OrderBy.Item item : orderBy.getItems()) {
					ColumnMeta columnMeta = tableMeta.getColumns().get(item.getField());
					if (columnMeta == null) {
						continue;
					}
					sql.orderBy(columnMeta.getColumnName() + " " + item.getDirection().getSqlText());
				}
			}
		}
		return sql.toSqlString();
	}

	private static void appendSqlWhereWithEntity(Map<String, Object> bindings, Class<?> entityClass
		, TableMeta tableMeta, SqlStatement sql, VarNameGenerator whereKeyGen
		, Object entity, Predicate<String> isIncludeColumns, Predicate<String> isExcludeColumns
		, boolean includeAllEmpty, Predicate<String> isIncludeEmptyColumns) {
		// 实体查询条件
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();

			// 不在包含列表
			if (isIncludeColumns != null && !isIncludeColumns.test(name)) {
				continue;
			}
			// 在排除列表
			if (isExcludeColumns != null && isExcludeColumns.test(name)) {
				continue;
			}

			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			Object val = entityMap.get(name);

			if (Objs.isNotEmpty(val)) {
				appendSqlWhereWithVal(bindings, sql, meta, val, whereKeyGen.generate());
			} else {
				boolean include =
					// 需要包含空值字段
					includeAllEmpty || (isIncludeEmptyColumns != null && isIncludeEmptyColumns.test(name));
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
			Function<String, String> columnDiscovery = field -> {
				ColumnMeta columnMeta = tableMeta.getColumns().get(field);
				if (columnMeta != null) {
					return columnMeta.getColumnName();
				}
				return null;
			};
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
		, @Nonnull SqlStatement sql, @Nonnull ColumnMeta meta, @Nonnull Object val, String key) {
		String columnName = meta.getColumnName();
		Class<?> fieldType = meta.getFieldType();
		// 日期字段
		if (Date.class.isAssignableFrom(fieldType)) {
			// 两个元素的日期类字段特殊处理，认为是日期范围条件
			Date[] range = EntityStatements.extractDateRange(val);
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

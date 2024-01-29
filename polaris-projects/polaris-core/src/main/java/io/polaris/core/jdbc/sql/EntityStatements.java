package io.polaris.core.jdbc.sql;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import io.polaris.core.collection.Iterables;
import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.annotation.EntityDelete;
import io.polaris.core.jdbc.sql.annotation.EntityInsert;
import io.polaris.core.jdbc.sql.annotation.EntityMerge;
import io.polaris.core.jdbc.sql.annotation.EntitySelect;
import io.polaris.core.jdbc.sql.annotation.EntityUpdate;
import io.polaris.core.jdbc.sql.annotation.SqlSelect;
import io.polaris.core.jdbc.sql.annotation.segment.SelectColumn;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.query.Queries;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.ConfigurableColumnPredicate;
import io.polaris.core.jdbc.sql.statement.DeleteStatement;
import io.polaris.core.jdbc.sql.statement.InsertStatement;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.jdbc.sql.statement.UpdateStatement;
import io.polaris.core.jdbc.sql.statement.segment.SelectSegment;
import io.polaris.core.jdbc.sql.statement.segment.WhereSegment;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.string.Strings;

import static io.polaris.core.lang.Objs.isNotEmpty;

/**
 * @author Qt
 * @since 1.8,  Jan 27, 2024
 */
@SuppressWarnings({"all"})
public class EntityStatements {

	public static final String DEFAULT_TABLE_ALIAS = "T";

	public static TableMeta getTableMeta(String entityClassName) {
		try {
			Class<?> type = Class.forName(entityClassName);
			return TableMetaKit.instance().get(type);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static SelectStatement<?> buildSelect(Map<String, Object> bindings, SqlSelect sqlSelect) {
		Class<?> entityClass = sqlSelect.value();
		SelectStatement<?> st = new SelectStatement<>(entityClass, Strings.coalesce(sqlSelect.tableAlias(), DEFAULT_TABLE_ALIAS));

//		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);


		// select
		{
			SelectColumn[] columns = sqlSelect.columns();
			if (columns != null && columns.length > 0) {
				for (SelectColumn col : columns) {
					String raw = col.raw();
					if (Strings.isNotBlank(raw)) {
						st.selectRaw(raw);
						continue;
					}
					String field = col.field();
					SelectSegment<?, ?> seg = st.select();
					if (Strings.isNotBlank(field)) {
						seg.column(field);
						String function = col.function();
						if (Strings.isNotBlank(function)) {
							seg.apply(function, bindings);
						}
						seg.aliasWithField(col.aliasWithField());
						seg.alias(col.alias());
					} else {
						String valueKey = col.valueKey();
						if (Strings.isNotBlank(valueKey)) {
							Object v = getObjectOfKey(bindings, valueKey, null);
							seg.value(v, col.alias());
						} else {
							throw new IllegalStateException("未指定字段名或固定键值");
						}
					}
				}
			} else {
				st.selectAll();
			}
			st.quotaSelectAlias(sqlSelect.quotaSelectAlias());
		}

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			sqlSelect.includeColumns(), sqlSelect.includeColumnsKey(),
			sqlSelect.excludeColumns(), sqlSelect.excludeColumnsKey(),
			sqlSelect.includeEmptyColumns(), sqlSelect.includeEmptyColumnsKey(),
			sqlSelect.includeAllEmpty(), sqlSelect.includeAllEmptyKey());


		// order by
		{
			io.polaris.core.jdbc.sql.annotation.segment.OrderBy[] orderBys = sqlSelect.orderBy();
			if (orderBys.length > 0) {
				for (io.polaris.core.jdbc.sql.annotation.segment.OrderBy orderBy : orderBys) {
					String field = orderBy.field();
					if (Strings.isBlank(field)) {
						throw new IllegalStateException("未指定排序字段名");
					}
					switch (orderBy.direction()) {
						case ASC:
							st.orderBy(field);
							break;
						case DESC:
							st.orderByDesc(field);
							break;
					}
				}
			} else {
				String orderByKey = sqlSelect.orderByKey();
				if (Strings.isNotBlank(orderByKey)) {
					Object orderByObj = getObjectOfKey(bindings, orderByKey, null);

					OrderBy orderBy = null;
					if (orderByObj instanceof String) {
						orderBy = Queries.newOrderBy((String) orderByObj);
					} else if (orderByObj instanceof OrderBy) {
						orderBy = (OrderBy) orderByObj;
					}
					if (orderBy != null) {
						st.orderBy(orderBy);
					}
				}
			}
		}

		return st;
	}

	public static InsertStatement<?> buildInsert(Map<String, Object> bindings, EntityInsert entityInsert) {
		return buildInsert(bindings, entityInsert.value(), entityInsert.entityKey()
			, entityInsert.includeColumns(), entityInsert.includeColumnsKey()
			, entityInsert.excludeColumns(), entityInsert.excludeColumnsKey()
			, entityInsert.includeEmptyColumns(), entityInsert.includeEmptyColumnsKey()
			, entityInsert.includeAllEmpty(), entityInsert.includeAllEmptyKey()
			, entityInsert.enableReplace(), entityInsert.enableUpdateByDuplicateKey());
	}

	public static InsertStatement<?> buildInsert(Map<String, Object> bindings, Class<?> entityClass, String entityKey,
		String[] includeColumns, String includeColumnsKey,
		String[] excludeColumns, String excludeColumnsKey,
		String[] includeEmptyColumns, String includeEmptyColumnsKey,
		boolean includeAllEmpty, String includeAllEmptyKey,
		boolean enableReplace, boolean enableUpdateByDuplicateKey
	) {

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			includeColumns, includeColumnsKey,
			excludeColumns, excludeColumnsKey,
			includeEmptyColumns, includeEmptyColumnsKey,
			includeAllEmpty, includeAllEmptyKey);

		return buildInsert(bindings, entityClass, entityKey, enableReplace, enableUpdateByDuplicateKey, columnPredicate);
	}

	public static InsertStatement<?> buildInsert(Map<String, Object> bindings, Class<?> entityClass, String entityKey, boolean enableReplace, boolean enableUpdateByDuplicateKey, ColumnPredicate columnPredicate) {
		Object entity = EntityStatements.getObjectOfKey(bindings, entityKey, Collections.emptyMap());
		InsertStatement<?> st = new InsertStatement<>(entityClass);
		st.withEntity(entity, columnPredicate);
		if (enableReplace) {
			st.enableReplace(true);
		}
		if (enableUpdateByDuplicateKey) {
			st.enableUpdateByDuplicateKey(true);
		}
		return st;
	}

	public static DeleteStatement<?> buildDelete(Map<String, Object> bindings, EntityDelete entityDelete) {
		return buildDelete(bindings, entityDelete.value()
			, Strings.trimToNull(entityDelete.tableAlias())
			, entityDelete.byId(), entityDelete.entityKey(), entityDelete.whereKey()
			, entityDelete.includeColumns(), entityDelete.includeColumnsKey()
			, entityDelete.excludeColumns(), entityDelete.excludeColumnsKey()
			, entityDelete.includeEmptyColumns(), entityDelete.includeEmptyColumnsKey()
			, entityDelete.includeAllEmpty(), entityDelete.includeAllEmptyKey());
	}

	public static DeleteStatement<?> buildDelete(Map<String, Object> bindings, Class<?> entityClass, String tableAlias,
		boolean byId, String entityKey, String whereKey,
		String[] includeColumns, String includeColumnsKey,
		String[] excludeColumns, String excludeColumnsKey,
		String[] includeEmptyColumns, String includeEmptyColumnsKey,
		boolean includeAllEmpty, String includeAllEmptyKey
	) {
		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			includeColumns, includeColumnsKey,
			excludeColumns, excludeColumnsKey,
			includeEmptyColumns, includeEmptyColumnsKey,
			includeAllEmpty, includeAllEmptyKey);
		Predicate<String> isIncludeColumns = EntityStatements.getColumnPredicate(bindings, includeColumns, includeColumnsKey);
		Predicate<String> isExcludeColumns = EntityStatements.getColumnPredicate(bindings, excludeColumns, excludeColumnsKey);
		Predicate<String> isIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		includeAllEmpty = EntityStatements.isIncludeEmpty(bindings, includeAllEmpty, includeAllEmptyKey);

		return buildDelete(bindings, entityClass, tableAlias, byId, entityKey, whereKey, columnPredicate);
	}

	public static DeleteStatement<?> buildDelete(Map<String, Object> bindings, Class<?> entityClass, String tableAlias, boolean byId, String entityKey, String whereKey, ColumnPredicate columnPredicate) {
		DeleteStatement<?> st = new DeleteStatement<>(entityClass, Strings.coalesce(tableAlias, DEFAULT_TABLE_ALIAS));
		if (byId) {
			Object entity = getObjectOfKey(bindings, entityKey, null);
			if (entity == null) {
				entity = EntityStatements.getObjectOfKey(bindings, whereKey, Collections.emptyMap());
			}
			st.where().byEntityId(entity);
		} else {
			Object entity = getObjectOfKey(bindings, entityKey, null);
			if (entity != null) {
				if (entity instanceof Criteria) {
					st.where((Criteria) entity);
				} else {
					st.where().byEntity(entity, columnPredicate);
				}
			}
			entity = getObjectOfKey(bindings, whereKey, null);
			if (entity != null) {
				if (entity instanceof Criteria) {
					st.where((Criteria) entity);
				} else {
					st.where().byEntity(entity, columnPredicate);
				}
			}
		}
		return st;
	}


	public static UpdateStatement<?> buildUpdate(Map<String, Object> bindings, EntityUpdate entityUpdate) {
		return buildUpdate(bindings, entityUpdate.value()
			, Strings.trimToNull(entityUpdate.tableAlias())
			, entityUpdate.byId(), entityUpdate.entityKey(), entityUpdate.whereKey()
			, entityUpdate.includeColumns(), entityUpdate.includeColumnsKey()
			, entityUpdate.excludeColumns(), entityUpdate.excludeColumnsKey()
			, entityUpdate.includeEmptyColumns(), entityUpdate.includeEmptyColumnsKey()
			, entityUpdate.includeAllEmpty(), entityUpdate.includeAllEmptyKey()
			, entityUpdate.whereIncludeColumns(), entityUpdate.whereIncludeColumnsKey()
			, entityUpdate.whereExcludeColumns(), entityUpdate.whereExcludeColumnsKey()
			, entityUpdate.whereIncludeEmptyColumns(), entityUpdate.whereIncludeEmptyColumnsKey()
			, entityUpdate.whereIncludeAllEmpty(), entityUpdate.whereIncludeAllEmptyKey()
		);
	}

	public static UpdateStatement<?> buildUpdate(Map<String, Object> bindings, Class<?> entityClass, String tableAlias,
		boolean byId, String entityKey, String whereKey,
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

		UpdateStatement<?> st = new UpdateStatement<>(entityClass, Strings.coalesce(tableAlias, DEFAULT_TABLE_ALIAS));
		if (byId) {
			Object entity = getObjectOfKey(bindings, entityKey, null);
			if (entity == null) {
				entity = EntityStatements.getObjectOfKey(bindings, whereKey, Collections.emptyMap());
			}
			st.withEntity(entity, isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);
			st.where().byEntityId(entity);
		} else {

			Predicate<String> isWhereIncludeColumns = EntityStatements.getColumnPredicate(bindings, whereIncludeColumns, whereIncludeColumnsKey);
			Predicate<String> isWhereExcludeColumns = EntityStatements.getColumnPredicate(bindings, whereExcludeColumns, whereExcludeColumnsKey);
			Predicate<String> isWhereIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, whereIncludeEmptyColumns, whereIncludeEmptyColumnsKey);
			whereIncludeAllEmpty = EntityStatements.isIncludeEmpty(bindings, whereIncludeAllEmpty, whereIncludeAllEmptyKey);

			Object entity = EntityStatements.getObjectOfKey(bindings, entityKey, Collections.emptyMap());
			Object where = EntityStatements.getObjectOfKey(bindings, whereKey, Collections.emptyMap());

			st.withEntity(entity, isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);

			if (where instanceof Criteria) {
				st.where((Criteria) where);
			} else {
				st.where().byEntity(where
					, isWhereIncludeColumns, isWhereExcludeColumns
					, whereIncludeAllEmpty, isWhereIncludeEmptyColumns);
			}
		}

		return st;
	}


	public static SelectStatement<?> buildSelect(Map<String, Object> bindings, EntitySelect entitySelect) {
		return buildSelect(bindings, entitySelect.value()
			, Strings.trimToNull(entitySelect.tableAlias())
			, entitySelect.byId(), entitySelect.entityKey()
			, entitySelect.whereKey(), entitySelect.orderByKey()
			, entitySelect.includeColumns(), entitySelect.includeColumnsKey()
			, entitySelect.excludeColumns(), entitySelect.excludeColumnsKey()
			, entitySelect.includeEmptyColumns(), entitySelect.includeEmptyColumnsKey()
			, entitySelect.includeAllEmpty(), entitySelect.includeAllEmptyKey());
	}

	public static SelectStatement<?> buildSelect(Map<String, Object> bindings, Class<?> entityClass, String tableAlias,
		boolean byId, String entityKey, String whereKey, String orderByKey,
		String[] includeColumns, String includeColumnsKey,
		String[] excludeColumns, String excludeColumnsKey,
		String[] includeEmptyColumns, String includeEmptyColumnsKey,
		boolean includeAllEmpty, String includeAllEmptyKey
	) {
		Predicate<String> isIncludeColumns = EntityStatements.getColumnPredicate(bindings, includeColumns, includeColumnsKey);
		Predicate<String> isExcludeColumns = EntityStatements.getColumnPredicate(bindings, excludeColumns, excludeColumnsKey);
		Predicate<String> isIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		includeAllEmpty = EntityStatements.isIncludeEmpty(bindings, includeAllEmpty, includeAllEmptyKey);

		SelectStatement<?> st = new SelectStatement<>(entityClass, Strings.coalesce(tableAlias, DEFAULT_TABLE_ALIAS));
		st.selectAll();
		if (byId) {
			Object entity = getObjectOfKey(bindings, entityKey, null);
			if (entity == null) {
				entity = EntityStatements.getObjectOfKey(bindings, whereKey, Collections.emptyMap());
			}
			st.where().byEntityId(entity);
		} else {
			Object entity = getObjectOfKey(bindings, entityKey, null);
			if (entity != null) {
				if (entity instanceof Criteria) {
					st.where((Criteria) entity);
				} else {
					st.where().byEntity(entity, isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);
				}
			}
			entity = getObjectOfKey(bindings, whereKey, null);
			if (entity != null) {
				if (entity instanceof Criteria) {
					st.where((Criteria) entity);
				} else {
					st.where().byEntity(entity, isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);
				}
			}
		}

		// 排序字段
		Object orderByObj = getObjectOfKey(bindings, orderByKey, null);
		OrderBy orderBy = null;
		if (orderByObj instanceof String) {
			orderBy = Queries.newOrderBy((String) orderByObj);
		} else if (orderByObj instanceof OrderBy) {
			orderBy = (OrderBy) orderByObj;
		}
		if (orderBy != null) {
			st.orderBy(orderBy);
		}
		return st;
	}

	public static MergeStatement<?> buildMerge(Map<String, Object> bindings, EntityMerge entityMerge) {
		return buildMerge(bindings, entityMerge.value()
			, Strings.trimToNull(entityMerge.tableAlias())
			, entityMerge.entityKey(), entityMerge.updateWhenMatched(), entityMerge.insertWhenNotMatched()
			, entityMerge.includeColumns(), entityMerge.includeColumnsKey()
			, entityMerge.excludeColumns(), entityMerge.excludeColumnsKey()
			, entityMerge.includeEmptyColumns(), entityMerge.includeEmptyColumnsKey()
			, entityMerge.includeAllEmpty(), entityMerge.includeAllEmptyKey());
	}

	public static MergeStatement<?> buildMerge(Map<String, Object> bindings, Class<?> entityClass,
		String tableAlias, String entityKey,
		boolean updateWhenMatched, boolean insertWhenNotMatched,
		String[] includeColumns, String includeColumnsKey,
		String[] excludeColumns, String excludeColumnsKey,
		String[] includeEmptyColumns, String includeEmptyColumnsKey,
		boolean includeAllEmpty, String includeAllEmptyKey
	) {
		Predicate<String> isIncludeColumns = EntityStatements.getColumnPredicate(bindings, includeColumns, includeColumnsKey);
		Predicate<String> isExcludeColumns = EntityStatements.getColumnPredicate(bindings, excludeColumns, excludeColumnsKey);
		Predicate<String> isIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		includeAllEmpty = EntityStatements.isIncludeEmpty(bindings, includeAllEmpty, includeAllEmptyKey);

		MergeStatement<?> st = new MergeStatement<>(entityClass, Strings.coalesce(tableAlias, DEFAULT_TABLE_ALIAS));
		Object entity = getObjectOfKey(bindings, entityKey, Collections.emptyMap());
		st.withEntity(entity, updateWhenMatched, insertWhenNotMatched,
			isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);
		return st;
	}

	public static String getSqlWithBindings(Map<String, Object> map, SqlNodeBuilder sqlNodeBuilder) {
		SqlNode sqlNode = sqlNodeBuilder.toSqlNode();
		return getSqlWithBindings(map, sqlNode);
	}

	public static String getSqlWithBindings(Map<String, Object> map, SqlNode sqlNode) {
		BoundSql boundSql = sqlNode.asBoundSql();
		Map<String, Object> bindings = boundSql.getBindings();
		if (bindings != null && !bindings.isEmpty()) {
			map.putAll(bindings);
		}
		return boundSql.getText();
	}


	static boolean isIncludeEmpty(Map<String, Object> bindings, boolean includeAllEmpty, String includeAllEmptyKey) {
		if (!includeAllEmpty) {
			if (Strings.isNotBlank(includeAllEmptyKey)) {
				Object val = getObjectOfKey(bindings, includeAllEmptyKey, null);
				if (val instanceof Boolean) {
					includeAllEmpty = ((Boolean) val).booleanValue();
				}
			}
		}
		return includeAllEmpty;
	}

	static Predicate<String> getColumnPredicate(Map<String, Object> bindings, String[] columns, String keyColumns) {
		Predicate<String> predicate = null;
		if (columns == null || columns.length == 0) {
			if (Strings.isNotBlank(keyColumns)) {
				Object val = getObjectOfKey(bindings, keyColumns, null);
				if (val instanceof String[]) {
					columns = (String[]) val;
				}
			}
		}
		if (columns != null && columns.length > 0) {
			Set<String> set = Iterables.asSet(columns);
			predicate = set::contains;
		}
		return predicate;
	}

	public static Object getObjectOfKey(Map<String, Object> bindings, String key, Object defVal) {
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


	static Object createTimeVal(Class<?> fieldType) {
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

	/**
	 * 提取日期范围的查询条件，值对象必须是两个元素的数组或集合，且不能都是空元素。
	 * 存在则返回两元素数组，否则返回Null
	 */
	public static Date[] extractDateRange(Object val) {
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

	public static void addWhereSqlByEntity(WhereSegment<?, ?> where, Object entity, TableMeta tableMeta
		, Predicate<String> isIncludeColumns, Predicate<String> isExcludeColumns
		, boolean includeAllEmpty, Predicate<String> isIncludeEmptyColumns) {
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		addWhereSqlByEntity(where, entityMap, tableMeta,
			isIncludeColumns, isExcludeColumns, includeAllEmpty, isIncludeEmptyColumns);
	}

	public static void addWhereSqlByEntity(WhereSegment<?, ?> where, Object entity, TableMeta tableMeta
		, ColumnPredicate columnPredicate) {
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		addWhereSqlByEntity(where, entityMap, tableMeta, columnPredicate);
	}

	private static void addWhereSqlByEntity(WhereSegment<?, ?> where, Map<String, Object> entityMap
		, TableMeta tableMeta
		, Predicate<String> isIncludeColumns, Predicate<String> isExcludeColumns
		, boolean includeAllEmpty, Predicate<String> isIncludeEmptyColumns) {
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
			Object val = entityMap.get(name);
			addWhereSqlByColumnValue(where, meta, val, includeAllEmpty, isIncludeEmptyColumns);
		}
	}

	private static void addWhereSqlByEntity(WhereSegment<?, ?> where, Map<String, Object> entityMap
		, TableMeta tableMeta
		, ColumnPredicate columnPredicate) {
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}
			ColumnMeta meta = entry.getValue();
			Object val = entityMap.get(name);
			addWhereSqlByColumnValue(where, meta, val, columnPredicate);
		}
	}

	private static void addWhereSqlByColumnValue(WhereSegment<?, ?> where, ColumnMeta meta, Object val
		, boolean includeAllEmpty, Predicate<String> isIncludeEmptyColumns) {
		if (isNotEmpty(val)) {
			Class<?> fieldType = meta.getFieldType();
			// 日期字段
			if (Date.class.isAssignableFrom(fieldType)) {
				Date[] range = extractDateRange(val);
				if (range != null) {
					if (range[0] != null) {
						where.column(meta.getFieldName()).ge(range[0]);
					}
					if (range[1] != null) {
						where.column(meta.getFieldName()).le(range[1]);
					}
					// 完成条件绑定
					return;
				}
			}
			// 文本字段
			else if (String.class.isAssignableFrom(fieldType)) {
				if (val instanceof String && (((String) val).startsWith("%") || ((String) val).endsWith("%"))) {
					where.column(meta.getFieldName()).like((String) val);
					// 完成条件绑定
					return;
				}
			}
			if (val instanceof Collection) {
				List<Object> list = new ArrayList<>((Collection<?>) val);
				where.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else if (val instanceof Iterable) {
				List<Object> list = Iterables.asCollection(ArrayList::new, (Iterable<Object>) val);
				where.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else if (val instanceof Iterator) {
				List<Object> list = Iterables.asCollection(ArrayList::new, (Iterator<Object>) val);
				where.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else if (val.getClass().isArray()) {
				List<Object> list = ObjectArrays.toList(val);
				where.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else {
				where.column(meta.getFieldName()).eq((Object) Converters.convertQuietly(fieldType, val));
			}
		} else {
			boolean include =
				// 需要包含空值字段
				includeAllEmpty || (isIncludeEmptyColumns != null && isIncludeEmptyColumns.test(meta.getFieldName()));
			if (include) {
				where.column(meta.getFieldName()).isNull();
			}
		}
	}

	private static void addWhereSqlByColumnValue(WhereSegment<?, ?> where, ColumnMeta meta, Object val
		, ColumnPredicate columnPredicate) {
		if (isNotEmpty(val)) {
			Class<?> fieldType = meta.getFieldType();
			// 日期字段
			if (Date.class.isAssignableFrom(fieldType)) {
				Date[] range = extractDateRange(val);
				if (range != null) {
					if (range[0] != null) {
						where.column(meta.getFieldName()).ge(range[0]);
					}
					if (range[1] != null) {
						where.column(meta.getFieldName()).le(range[1]);
					}
					// 完成条件绑定
					return;
				}
			}
			// 文本字段
			else if (String.class.isAssignableFrom(fieldType)) {
				if (val instanceof String && (((String) val).startsWith("%") || ((String) val).endsWith("%"))) {
					where.column(meta.getFieldName()).like((String) val);
					// 完成条件绑定
					return;
				}
			}
			if (val instanceof Collection) {
				List<Object> list = new ArrayList<>((Collection<?>) val);
				where.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else if (val instanceof Iterable) {
				List<Object> list = Iterables.asCollection(ArrayList::new, (Iterable<Object>) val);
				where.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else if (val instanceof Iterator) {
				List<Object> list = Iterables.asCollection(ArrayList::new, (Iterator<Object>) val);
				where.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else if (val.getClass().isArray()) {
				List<Object> list = ObjectArrays.toList(val);
				where.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else {
				where.column(meta.getFieldName()).eq((Object) Converters.convertQuietly(fieldType, val));
			}
		} else {
			// 需要包含空值字段
			if (columnPredicate.isIncludedEmptyColumn(meta.getFieldName())) {
				where.column(meta.getFieldName()).isNull();
			}
		}
	}

	private static List<Object> convertListElements(List<Object> list, Function<Object, Object> converter) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Object o = list.get(i);
			list.set(i, converter.apply(o));
		}
		return list;
	}


	@Nullable
	public static Object getValForInsert(Map<String, Object> entityMap, ColumnMeta meta) {
		Object val = entityMap.get(meta.getFieldName());
		if (val == null) {
			if (meta.isCreateTime() || meta.isUpdateTime()) {
				Object value = new Date();
				val = Converters.convertQuietly(meta.getFieldType(), value);
			}
		}
		if (val == null) {
			String insertDefault = meta.getInsertDefault();
			if (Strings.isNotBlank(insertDefault)) {
				val = Converters.convertQuietly(meta.getFieldType(), insertDefault);
			}
		}
		return val;
	}


	@Nullable
	public static Object getValForUpdate(Map<String, Object> entityMap, ColumnMeta meta) {
		Object val = entityMap.get(meta.getFieldName());
		if (val == null) {
			if (meta.isUpdateTime()) {
				Object value = new Date();
				val = Converters.convertQuietly(meta.getFieldType(), value);
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

}

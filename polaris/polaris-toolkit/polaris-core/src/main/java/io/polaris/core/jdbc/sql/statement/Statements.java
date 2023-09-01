package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.collection.Iterables;
import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.statement.segment.TableField;
import io.polaris.core.jdbc.sql.statement.segment.WhereSegment;
import io.polaris.core.jdbc.table.DualEntity;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.string.Strings;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

import static io.polaris.core.lang.Objs.isNotEmpty;

/**
 * @author Qt
 * @since 1.8,  Aug 31, 2023
 */
public class Statements {

	public static final Predicate<String> PREDICATE_EXCLUDE_NULLS = name -> false;

	public static TableMeta getTableMeta(String entityClassName) {
		try {
			Class<?> type = Class.forName(entityClassName);
			return TableMetaKit.instance().get(type);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static SelectStatement<?> buildSelectStatement(Class<?> entityClass, String tableAlias, Object entity) {
		return buildSelectStatement(entityClass, tableAlias, entity, PREDICATE_EXCLUDE_NULLS, null, null);
	}

	public static SelectStatement<?> buildSelectStatement(Class<?> entityClass, String tableAlias, Object entity, OrderBy orderBy) {
		return buildSelectStatement(entityClass, tableAlias, entity, PREDICATE_EXCLUDE_NULLS, null, orderBy);
	}

	public static SelectStatement<?> buildSelectStatement(Class<?> entityClass, String tableAlias, Object entity, Criteria criteria, OrderBy orderBy) {
		return buildSelectStatement(entityClass, tableAlias, entity, PREDICATE_EXCLUDE_NULLS, criteria, orderBy);
	}

	public static SelectStatement<?> buildSelectStatement(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeWhereNulls, Criteria criteria, OrderBy orderBy) {
		SelectStatement<?> st = new SelectStatement<>(entityClass, tableAlias);
		st.selectAll();
		st.where(criteria);
		st.orderBy(orderBy);

		if (entity != null) {
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
			TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
			WhereSegment<?, ?> where = st.where();
			addSqlWhereByEntity(where, entityMap, tableMeta, includeWhereNulls);
		}

		return st;
	}

	public static InsertStatement<?> buildInsertStatement(Class<?> entityClass, Object entity) {
		return buildInsertStatement(entityClass, entity, PREDICATE_EXCLUDE_NULLS);
	}

	public static InsertStatement<?> buildInsertStatement(Class<?> entityClass, Object entity, Predicate<String> includeEntityNulls) {
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);

		InsertStatement<?> st = new InsertStatement<>(entityClass);
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			boolean insertable = meta.isInsertable() || meta.isCreateTime() || meta.isUpdateTime();
			if (!insertable) {
				continue;
			}
			Object val = getValForInsert(entityMap, meta);
			if (isNotEmpty(val) || includeEntityNulls.test(name)) {
				st.column(name, val);
			}
		}
		return st;
	}

	public static UpdateStatement<?> buildUpdateStatement(Class<?> entityClass, String tableAlias, Object entity) {
		return buildUpdateStatement(entityClass, tableAlias, entity, PREDICATE_EXCLUDE_NULLS);
	}

	public static UpdateStatement<?> buildUpdateStatement(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeEntityNulls) {
		UpdateStatement<?> st = new UpdateStatement<>(entityClass, tableAlias);
		if (entity instanceof Criteria) {
			st.where((Criteria) entity);
		} else {
			TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				ColumnMeta meta = entry.getValue();
				if (meta.isPrimaryKey() || meta.isVersion()) {
					Object val = entityMap.get(name);
					if (val == null) {
						st.where().column(name).isNull();
					} else {
						st.where().column(name).eq(val);
					}
				}
				boolean updatable = (meta.isUpdatable() || meta.isVersion() || meta.isUpdateTime());
				if (!updatable) {
					continue;
				}

				Object val = getValForUpdate(entityMap, meta);
				if (meta.isVersion()) {
					val = val == null ? 1L : ((Number) val).longValue() + 1;
				} else if (meta.isPrimaryKey()) {
					// skip set
					continue;
				}

				if (isNotEmpty(val) || includeEntityNulls.test(name)) {
					st.column(name, val);
				}
			}
		}
		return st;
	}

	public static DeleteStatement<?> buildDeleteStatement(Class<?> entityClass, String tableAlias, Object entity) {
		return buildDeleteStatement(entityClass, tableAlias, entity, PREDICATE_EXCLUDE_NULLS);
	}

	public static DeleteStatement<?> buildDeleteStatement(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeWhereNulls) {
		DeleteStatement<?> st = new DeleteStatement<>(entityClass, tableAlias);
		if (entity instanceof Criteria) {
			st.where((Criteria) entity);
		} else {
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
			TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
			WhereSegment<?, ?> where = st.where();
			addSqlWhereByEntity(where, entityMap, tableMeta, includeWhereNulls);
		}
		return st;
	}

	public static MergeStatement<?> buildMergeStatement(Class<?> entityClass, Object entity) {
		return buildMergeStatement(entityClass, entity, PREDICATE_EXCLUDE_NULLS);
	}

	public static MergeStatement<?> buildMergeStatement(Class<?> entityClass, Object entity, Predicate<String> includeEntityNulls) {
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);

		MergeStatement<?> st = new MergeStatement<>(entityClass, "t");
		SelectStatement<?> using = new SelectStatement<>(DualEntity.class);
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			if (meta.isPrimaryKey()) {
				Object val = getValForInsert(entityMap, meta);
				using.select().column(DualEntity.Fields.dummy).value(val, name);
			}
		}
		st.using(using, "s");
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			if (meta.isPrimaryKey()) {
				st.on().column(name).eq(TableField.of("s", name));
			}
		}
		st.updateWhenMatched();
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			boolean updatable = !meta.isPrimaryKey() && (meta.isUpdatable() || meta.isUpdateTime());
			if (!updatable) {
				continue;
			}
			Object val = getValForUpdate(entityMap, meta);
			if (isNotEmpty(val) || includeEntityNulls.test(name)) {
				st.update(name, val);
			}
		}
		st.insertWhenNotMatched();
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			boolean insertable = meta.isInsertable() || meta.isCreateTime() || meta.isUpdateTime();
			if (!insertable) {
				continue;
			}
			Object val = getValForInsert(entityMap, meta);

			if (isNotEmpty(val) || includeEntityNulls.test(name)) {
				st.insert(name, val);
			}
		}
		return st;
	}


	@Nullable
	public static Object getValForInsert(Map<String, Object> entityMap, ColumnMeta meta) {
		Object val = entityMap.get(meta.getFieldName());
		if (val == null) {
			if (meta.isCreateTime() || meta.isUpdateTime()) {
				val = ConverterRegistry.INSTANCE.convertQuietly(meta.getFieldType(), new Date());
			}
		}
		if (val == null) {
			String insertDefault = meta.getInsertDefault();
			if (Strings.isNotBlank(insertDefault)) {
				val = ConverterRegistry.INSTANCE.convertQuietly(meta.getFieldType(), insertDefault);
			}
		}
		return val;
	}

	@Nullable
	public static Object getValForUpdate(Map<String, Object> entityMap, ColumnMeta meta) {
		Object val = entityMap.get(meta.getFieldName());
		if (val == null) {
			if (meta.isUpdateTime()) {
				val = ConverterRegistry.INSTANCE.convertQuietly(meta.getFieldType(), new Date());
			}
		}
		if (val == null) {
			String updateDefault = meta.getUpdateDefault();
			if (Strings.isNotBlank(updateDefault)) {
				val = ConverterRegistry.INSTANCE.convertQuietly(meta.getFieldType(), updateDefault);
			}
		}
		return val;
	}

	/**
	 * 提取日期范围的查询条件，值对象必须是两个元素的数组或集合，且不能都是空元素。
	 * 存在则返回两元素数组，否则返回Null
	 */
	public static Date[] extractDateRange(Object val) {
		Date[] range = new Date[2];
		// 两个元素的日期类字段特殊处理，认为是日期范围条件
		if (val instanceof Iterable) {
			Iterator<?> iter = ((Iterable<?>) val).iterator();
			if (iter.hasNext()) {
				Object next = iter.next();
				if (next == null || next instanceof Date) {
					range[0] = (Date) next;
				} else {
					return null;
				}
			}
			if (iter.hasNext()) {
				Object next = iter.next();
				if (next == null || next instanceof Date) {
					range[1] = (Date) next;
				} else {
					return null;
				}
			}
			if (iter.hasNext()) {
				return null;
			}
		} else if (val.getClass().isArray()) {
			int len = Array.getLength(val);
			if (len == 2) {
				Object start = Array.get(val, 0);
				Object end = Array.get(val, 1);
				if (start == null || start instanceof Date) {
					range[0] = (Date) start;
				}
				if (end == null || end instanceof Date) {
					range[1] = (Date) end;
				}
			} else {
				return null;
			}
		}
		if (range[0] == null && range[1] == null) {
			return null;
		}
		return range;
	}

	private static void addSqlWhereByEntity(WhereSegment<?, ?> where, Map<String, Object> entityMap
		, TableMeta tableMeta, Predicate<String> includeWhereNulls) {
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			Object val = entityMap.get(name);
			addSqlWhereByColumnValue(where, meta, val, includeWhereNulls);
		}
	}

	private static void addSqlWhereByColumnValue(WhereSegment<?, ?> where, ColumnMeta meta
		, Object val, Predicate<String> includeWhereNulls) {
		if (isNotEmpty(val)) {
			// 日期字段
			if (Date.class.isAssignableFrom(meta.getFieldType())) {
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
			else if (String.class.isAssignableFrom(meta.getFieldType())) {
				if (val instanceof String && (((String) val).startsWith("%") || ((String) val).endsWith("%"))) {
					where.column(meta.getFieldName()).like((String) val);
					// 完成条件绑定
					return;
				}
			}

			if (val instanceof Collection) {
				where.column(meta.getFieldName()).in((Collection) val);
			} else if (val instanceof Iterable) {
				where.column(meta.getFieldName()).in(Iterables.asList((Iterable) val));
			} else if (val instanceof Iterator) {
				where.column(meta.getFieldName()).in(Iterables.asList((Iterator) val));
			} else if (val.getClass().isArray()) {
				where.column(meta.getFieldName()).in(ObjectArrays.toList(val));
			} else {
				where.column(meta.getFieldName()).eq(val);
			}
		} else if (includeWhereNulls.test(meta.getFieldName())) {
			where.column(meta.getFieldName()).isNull();
		}
	}

}

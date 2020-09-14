package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.collection.Iterables;
import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.statement.segment.WhereSegment;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.string.Strings;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.polaris.core.lang.Objs.isNotEmpty;

/**
 * @author Qt
 * @since 1.8,  Aug 31, 2023
 */
public class Statements {

	public static final Predicate<String> DEFAULT_PREDICATE_EXCLUDE_NULLS = name -> false;
	public static final String DEFAULT_TABLE_ALIAS = "T";

	public static TableMeta getTableMeta(String entityClassName) {
		try {
			Class<?> type = Class.forName(entityClassName);
			return TableMetaKit.instance().get(type);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static SelectStatement<?> buildSelect(Class<?> entityClass, Object entity) {
		return buildSelect(entityClass, DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS, null, null);
	}

	public static SelectStatement<?> buildSelect(Class<?> entityClass, String tableAlias, Object entity) {
		return buildSelect(entityClass, tableAlias, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS, null, null);
	}

	public static SelectStatement<?> buildSelect(Class<?> entityClass, Object entity, OrderBy orderBy) {
		return buildSelect(entityClass, DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS, null, orderBy);
	}

	public static SelectStatement<?> buildSelect(Class<?> entityClass, String tableAlias, Object entity, OrderBy orderBy) {
		return buildSelect(entityClass, tableAlias, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS, null, orderBy);
	}

	public static SelectStatement<?> buildSelect(Class<?> entityClass, Object entity, Criteria criteria, OrderBy orderBy) {
		return buildSelect(entityClass, DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS, criteria, orderBy);
	}

	public static SelectStatement<?> buildSelect(Class<?> entityClass, String tableAlias, Object entity, Criteria criteria, OrderBy orderBy) {
		return buildSelect(entityClass, tableAlias, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS, criteria, orderBy);
	}

	public static SelectStatement<?> buildSelect(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeWhereNulls, Criteria criteria, OrderBy orderBy) {
		SelectStatement<?> st = new SelectStatement<>(entityClass, tableAlias);
		st.selectAll();
		st.where(criteria);
		st.orderBy(orderBy);
		st.where().byEntity(entity, includeWhereNulls);
		return st;
	}

	public static InsertStatement<?> buildInsert(Class<?> entityClass, Object entity) {
		return buildInsert(entityClass, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS);
	}

	public static InsertStatement<?> buildInsert(Class<?> entityClass, Object entity, Predicate<String> includeEntityNulls) {
		InsertStatement<?> st = new InsertStatement<>(entityClass);
		st.withEntity(entity, includeEntityNulls);
		return st;
	}

	public static UpdateStatement<?> buildUpdate(Class<?> entityClass, Object entity, Object where) {
		return buildUpdate(entityClass, DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS, where, DEFAULT_PREDICATE_EXCLUDE_NULLS);
	}

	public static UpdateStatement<?> buildUpdate(Class<?> entityClass, String tableAlias, Object entity, Object where) {
		return buildUpdate(entityClass, tableAlias, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS, where, DEFAULT_PREDICATE_EXCLUDE_NULLS);
	}

	public static UpdateStatement<?> buildUpdate(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeEntityNulls, Object where) {
		return buildUpdate(entityClass, tableAlias, entity, includeEntityNulls, where, DEFAULT_PREDICATE_EXCLUDE_NULLS);
	}

	public static UpdateStatement<?> buildUpdate(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeEntityNulls, Object where, Predicate<String> includeWhereNulls) {
		UpdateStatement<?> st = new UpdateStatement<>(entityClass, tableAlias);
		st.withEntity(entity, includeEntityNulls);
		if (where instanceof Criteria) {
			st.where((Criteria) where);
		} else {
			st.where().byEntity(where, includeWhereNulls);
		}
		return st;
	}

	public static UpdateStatement<?> buildUpdateById(Class<?> entityClass, Object entity) {
		return buildUpdateById(entityClass, DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS);
	}

	public static UpdateStatement<?> buildUpdateById(Class<?> entityClass, String tableAlias, Object entity) {
		return buildUpdateById(entityClass, tableAlias, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS);
	}

	public static UpdateStatement<?> buildUpdateById(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeEntityNulls) {
		UpdateStatement<?> st = new UpdateStatement<>(entityClass, tableAlias);
		st.withEntity(entity, includeEntityNulls);
		st.where().byEntityId(entity);
		return st;
	}

	public static DeleteStatement<?> buildDelete(Class<?> entityClass, Object entity) {
		return buildDelete(entityClass, DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS);
	}

	public static DeleteStatement<?> buildDelete(Class<?> entityClass, String tableAlias, Object entity) {
		return buildDelete(entityClass, tableAlias, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS);
	}

	public static DeleteStatement<?> buildDelete(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeWhereNulls) {
		DeleteStatement<?> st = new DeleteStatement<>(entityClass, tableAlias);
		if (entity instanceof Criteria) {
			st.where((Criteria) entity);
		} else {
			st.where().byEntity(entity, includeWhereNulls);
		}
		return st;
	}

	public static DeleteStatement<?> buildDeleteById(Class<?> entityClass, Object entity) {
		return buildDeleteById(entityClass, DEFAULT_TABLE_ALIAS, entity);
	}

	public static DeleteStatement<?> buildDeleteById(Class<?> entityClass, String tableAlias, Object entity) {
		DeleteStatement<?> st = new DeleteStatement<>(entityClass, tableAlias);
		st.where().byEntityId(entity);
		return st;
	}

	public static MergeStatement<?> buildMerge(Class<?> entityClass, Object entity) {
		return buildMerge(entityClass, entity, DEFAULT_PREDICATE_EXCLUDE_NULLS);
	}

	public static MergeStatement<?> buildMerge(Class<?> entityClass, Object entity, Predicate<String> includeEntityNulls) {
		MergeStatement<?> st = new MergeStatement<>(entityClass, DEFAULT_TABLE_ALIAS);
		st.withEntity(entity, includeEntityNulls);
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
		range[0] = ConverterRegistry.INSTANCE.convertQuietly(Date.class, couple[0]);
		range[1] = ConverterRegistry.INSTANCE.convertQuietly(Date.class, couple[1]);
		if (range[0] == null && range[1] == null) {
			return null;
		}
		return range;
	}

	public static void addWhereSqlByEntity(WhereSegment<?, ?> where, Object entity, TableMeta tableMeta) {
		addWhereSqlByEntity(where, entity, tableMeta, DEFAULT_PREDICATE_EXCLUDE_NULLS);
	}

	public static void addWhereSqlByEntity(WhereSegment<?, ?> where, Object entity, TableMeta tableMeta, Predicate<String> includeWhereNulls) {
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, tableMeta.getEntityClass());
		addWhereSqlByEntity(where, entityMap, tableMeta, includeWhereNulls);
	}

	private static void addWhereSqlByEntity(WhereSegment<?, ?> where, Map<String, Object> entityMap
		, TableMeta tableMeta, Predicate<String> includeWhereNulls) {
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			Object val = entityMap.get(name);
			addWhereSqlByColumnValue(where, meta, val, includeWhereNulls);
		}
	}

	private static void addWhereSqlByColumnValue(WhereSegment<?, ?> where, ColumnMeta meta
		, Object val, Predicate<String> includeWhereNulls) {
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
				where.column(meta.getFieldName()).in(convertListElements(list, o->ConverterRegistry.INSTANCE.convertQuietly(fieldType, o)));
			} else if (val instanceof Iterable) {
				List<Object> list = Iterables.asCollection(ArrayList::new, (Iterable<Object>) val);
				where.column(meta.getFieldName()).in(convertListElements(list, o->ConverterRegistry.INSTANCE.convertQuietly(fieldType, o)));
			} else if (val instanceof Iterator) {
				List<Object> list = Iterables.asCollection(ArrayList::new, (Iterator<Object>) val);
				where.column(meta.getFieldName()).in(convertListElements(list, o->ConverterRegistry.INSTANCE.convertQuietly(fieldType, o)));
			} else if (val.getClass().isArray()) {
				List<Object> list = ObjectArrays.toList(val);
				where.column(meta.getFieldName()).in(convertListElements(list, o->ConverterRegistry.INSTANCE.convertQuietly(fieldType, o)));
			} else {
				where.column(meta.getFieldName()).eq((Object)ConverterRegistry.INSTANCE.convertQuietly(fieldType, val));
			}
		} else if (includeWhereNulls.test(meta.getFieldName())) {
			where.column(meta.getFieldName()).isNull();
		}
	}

	private static List<Object> convertListElements(List<Object> list, Function<Object,Object> converter){
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Object o = list.get(i);
			list.set(i, converter.apply(o));
		}
		return list;
	}
}

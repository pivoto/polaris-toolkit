package io.polaris.core.jdbc.sql;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.annotation.EntityDelete;
import io.polaris.core.jdbc.sql.annotation.EntityInsert;
import io.polaris.core.jdbc.sql.annotation.EntityMerge;
import io.polaris.core.jdbc.sql.annotation.EntitySelect;
import io.polaris.core.jdbc.sql.annotation.EntityUpdate;
import io.polaris.core.jdbc.sql.annotation.SqlDelete;
import io.polaris.core.jdbc.sql.annotation.SqlInsert;
import io.polaris.core.jdbc.sql.annotation.SqlSelect;
import io.polaris.core.jdbc.sql.annotation.SqlSelectSet;
import io.polaris.core.jdbc.sql.annotation.SqlUpdate;
import io.polaris.core.jdbc.sql.annotation.segment.BindingKey;
import io.polaris.core.jdbc.sql.annotation.segment.Condition;
import io.polaris.core.jdbc.sql.annotation.segment.Criteria1;
import io.polaris.core.jdbc.sql.annotation.segment.Criteria2;
import io.polaris.core.jdbc.sql.annotation.segment.Criteria3;
import io.polaris.core.jdbc.sql.annotation.segment.Criteria4;
import io.polaris.core.jdbc.sql.annotation.segment.Criteria5;
import io.polaris.core.jdbc.sql.annotation.segment.Criterion;
import io.polaris.core.jdbc.sql.annotation.segment.GroupBy;
import io.polaris.core.jdbc.sql.annotation.segment.Having;
import io.polaris.core.jdbc.sql.annotation.segment.InsertColumn;
import io.polaris.core.jdbc.sql.annotation.segment.Join;
import io.polaris.core.jdbc.sql.annotation.segment.JoinColumn;
import io.polaris.core.jdbc.sql.annotation.segment.JoinCriterion;
import io.polaris.core.jdbc.sql.annotation.segment.SelectColumn;
import io.polaris.core.jdbc.sql.annotation.segment.SubCriteria;
import io.polaris.core.jdbc.sql.annotation.segment.SubCriteria1;
import io.polaris.core.jdbc.sql.annotation.segment.SubCriteria2;
import io.polaris.core.jdbc.sql.annotation.segment.SubCriteria3;
import io.polaris.core.jdbc.sql.annotation.segment.SubCriteria4;
import io.polaris.core.jdbc.sql.annotation.segment.SubCriteria5;
import io.polaris.core.jdbc.sql.annotation.segment.SubCriterion;
import io.polaris.core.jdbc.sql.annotation.segment.SubHaving;
import io.polaris.core.jdbc.sql.annotation.segment.SubSelect;
import io.polaris.core.jdbc.sql.annotation.segment.SubWhere;
import io.polaris.core.jdbc.sql.annotation.segment.UpdateColumn;
import io.polaris.core.jdbc.sql.annotation.segment.Where;
import io.polaris.core.jdbc.sql.consts.JoinType;
import io.polaris.core.jdbc.sql.consts.Relation;
import io.polaris.core.jdbc.sql.consts.SelectSetOps;
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
import io.polaris.core.jdbc.sql.statement.SetOpsStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.jdbc.sql.statement.UpdateStatement;
import io.polaris.core.jdbc.sql.statement.segment.CriterionSegment;
import io.polaris.core.jdbc.sql.statement.segment.JoinSegment;
import io.polaris.core.jdbc.sql.statement.segment.SelectSegment;
import io.polaris.core.jdbc.sql.statement.segment.TableField;
import io.polaris.core.jdbc.sql.statement.segment.WhereSegment;
import io.polaris.core.lang.Objs;
import io.polaris.core.regex.Patterns;
import io.polaris.core.script.Evaluator;
import io.polaris.core.script.GroovyEvaluator;
import io.polaris.core.script.JavaEvaluator;
import io.polaris.core.script.JavaScriptEvaluator;
import io.polaris.core.script.ScriptEvaluators;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Tuple1;
import io.polaris.core.tuple.ValueRef;

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

	public static InsertStatement<?> buildInsert(Map<String, Object> bindings, SqlInsert sqlInsert) {
		Class<?> entityClass = sqlInsert.table();
		InsertStatement<?> st = new InsertStatement<>(entityClass);

		// binding-cache
		Map<String, ValueRef<Object>> cache = new HashMap<>();
		// columns
		InsertColumn[] columns = sqlInsert.columns();
		Map<String, Tuple1<?>> values = new HashMap<>();
		for (InsertColumn column : columns) {
			String field = column.field();
			Tuple1<?> val = getValForBindingKey(cache, bindings, column.bindingKey());
			values.put(field, val);
		}
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String field = entry.getKey();
			ColumnMeta meta = entry.getValue();
			Tuple1<?> tuple = values.get(field);
			Object val = null;
			boolean enabled = false;
			if (tuple != null) {
				enabled = true;
				val = tuple.getFirst();
			}
			val = BindingValues.getValueForInsert(meta, val);
			if (enabled || val != null) {
				st.column(field, val);
			}
		}

		if (sqlInsert.enableReplace()) {
			st.enableReplace(true);
		}
		if (sqlInsert.enableUpdateByDuplicateKey()) {
			st.enableUpdateByDuplicateKey(true);
		}
		return st;
	}

	public static DeleteStatement<?> buildDelete(Map<String, Object> bindings, SqlDelete sqlDelete) {
		Class<?> entityClass = sqlDelete.table();
		if (entityClass == null || entityClass == void.class) {
			throw new IllegalStateException("实体类型不能为空");
		}
		DeleteStatement<?> st = new DeleteStatement<>(entityClass,
			Strings.coalesce(sqlDelete.alias(), DEFAULT_TABLE_ALIAS));
		// binding-cache
		Map<String, ValueRef<Object>> cache = new HashMap<>();
		// where
		addWhereClause(cache, bindings, st.where(), sqlDelete.where(), sqlDelete.columnPredicate());
		return st;
	}

	public static UpdateStatement<?> buildUpdate(Map<String, Object> bindings, SqlUpdate sqlUpdate) {
		Class<?> entityClass = sqlUpdate.table();
		UpdateStatement<?> st = new UpdateStatement<>(entityClass,
			Strings.coalesce(sqlUpdate.alias(), DEFAULT_TABLE_ALIAS));

		// binding-cache
		Map<String, ValueRef<Object>> cache = new HashMap<>();
		// columns
		UpdateColumn[] columns = sqlUpdate.columns();
		Map<String, Tuple1<?>> values = new HashMap<>();
		for (UpdateColumn column : columns) {
			String field = column.field();
			Tuple1<?> val = getValForBindingKey(cache, bindings, column.bindingKey());
			values.put(field, val);
		}
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String field = entry.getKey();
			ColumnMeta meta = entry.getValue();
			Tuple1<?> tuple = values.get(field);
			Object val = null;
			boolean enabled = false;
			if (tuple != null) {
				enabled = true;
				val = tuple.getFirst();
			}
			val = BindingValues.getValueForUpdate(meta, val);
			if (enabled || val != null) {
				st.column(field, val);
			}
		}

		// where
		addWhereClause(cache, bindings, st.where(), sqlUpdate.where(), sqlUpdate.columnPredicate());
		return st;
	}


	public static SetOpsStatement<?> buildSelectSet(Map<String, Object> bindings, SqlSelectSet sqlSelectSet) {
		// binding-cache
		Map<String, ValueRef<Object>> cache = new HashMap<>();
		SetOpsStatement<?> sos = null;
		for (SqlSelectSet.Item item : sqlSelectSet.value()) {
			SqlSelect sqlSelect = item.value();
			SelectStatement<?> st = buildSelect(cache, bindings, sqlSelect);
			if (sos == null) {
				sos = SetOpsStatement.of(st);
				continue;
			}
			SelectSetOps ops = item.ops();
			switch (ops) {
				case UNION:
					sos.union(st);
					break;
				case UNION_ALL:
					sos.unionAll(st);
					break;
				case INTERSECT:
					sos.intersect(st);
					break;
				case INTERSECT_ALL:
					sos.intersectAll(st);
					break;
				case MINUS:
					sos.minus(st);
					break;
				case MINUS_ALL:
					sos.minusAll(st);
					break;
				case EXCEPT:
					sos.except(st);
					break;
				case EXCEPT_ALL:
					sos.exceptAll(st);
					break;
				default:
			}
		}
		return sos;
	}

	public static SelectStatement<?> buildSelect(Map<String, Object> bindings, SqlSelect sqlSelect) {
		// binding-cache
		Map<String, ValueRef<Object>> cache = new HashMap<>();
		return buildSelect(cache, bindings, sqlSelect);
	}

	private static SelectStatement<?> buildSelect(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, SqlSelect sqlSelect) {
		Class<?> entityClass = sqlSelect.table();
		SelectStatement<?> st = new SelectStatement<>(entityClass, Strings.coalesce(sqlSelect.alias(), DEFAULT_TABLE_ALIAS));
		// select
		addSelectColumns(cache, bindings, st, sqlSelect.columns(), sqlSelect.quotaSelectAlias());

		// join
		addJoinClause(cache, bindings, st, sqlSelect.join());

		// where
		addWhereClause(cache, bindings, st.where(), sqlSelect.where(), sqlSelect.columnPredicate());
		// group by
		addGroupByClause(cache, bindings, st, sqlSelect.groupBy());

		// having
		addHavingClause(cache, bindings, st, sqlSelect.having());

		// order by
		addOrderByClause(cache, bindings, st, sqlSelect);

		return st;
	}

	private static void addSelectColumns(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, SelectStatement<?> st, SelectColumn[] columns, boolean quotaSelectAlias) {
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
						Object v = BindingValues.getBindingValueOrDefault(cache,bindings, valueKey, null);
						seg.value(v, col.alias());
					} else {
						throw new IllegalStateException("未指定字段名或固定键值");
					}
				}
			}
		} else {
			st.selectAll();
		}
		st.quotaSelectAlias(quotaSelectAlias);
	}

	private static void addJoinClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, SelectStatement<?> st, Join[] joins) {
		if (joins.length > 0) {
			for (Join join : joins) {
				Class<?> joinTable = join.table();
				String joinAlias = join.alias();
				if (joinTable == null || joinTable == void.class) {
					continue;
				}
				if (Strings.isBlank(joinAlias)) {
					continue;
				}
				JoinType joinType = join.type();
				JoinSegment<?, ?> joinSt =
					joinType == JoinType.JOIN ? st.join(joinTable, joinAlias) :
						joinType == JoinType.INNER_JOIN ? st.innerJoin(joinTable, joinAlias) :
							joinType == JoinType.LEFT_JOIN ? st.leftJoin(joinTable, joinAlias) :
								joinType == JoinType.RIGHT_JOIN ? st.rightJoin(joinTable, joinAlias) :
									st.outerJoin(joinTable, joinAlias);
				// select
				addJoinSelectColumns(cache, bindings, joinSt, join.columns());
				// on
				WhereSegment<?, ?> on = joinSt.on();
				for (io.polaris.core.jdbc.sql.annotation.segment.Criteria criteria : join.on()) {
					addWhereByCriteria(cache, bindings, criteria, on);
				}
				// where
				addJoinWhereClause(cache, bindings, joinSt, join);
				// group by
				addJoinGroupByClause(cache, bindings, joinSt, join.groupBy());
				// having
				addJoinHavingClause(cache, bindings, joinSt, join.having());
				// order by
				addJoinOrderByClause(cache, bindings, joinSt, join);

			}
		}
	}

	private static void addJoinSelectColumns(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, JoinSegment<?, ?> st, SelectColumn[] columns) {
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
						Object v = BindingValues.getBindingValueOrDefault(bindings, valueKey, null);
						seg.value(v, col.alias());
					} else {
						throw new IllegalStateException("未指定字段名或固定键值");
					}
				}
			}
		} else {
			st.selectAll();
		}
	}

	private static void addJoinWhereClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, JoinSegment<?, ?> st
		, Join join) {
		WhereSegment<?, ?> stWhere = st.where();
		Where where = join.where();
		String entityIdKey = where.byEntityIdKey();
		String entityKey = where.byEntityKey();

		if (Strings.isNotBlank(entityIdKey)) {
			// 存在主键条件时
			Object entity = BindingValues.getBindingValueOrDefault(cache, bindings, entityIdKey, Collections.emptyMap());
			stWhere.byEntityId(entity);
		}
		if (Strings.isNotBlank(entityKey)) {
			// 不存在主键条件时，使用实体全字段条件
			Object entity = BindingValues.getBindingValueOrDefault(cache, bindings, entityKey, Collections.emptyMap());
			ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings, join.columnPredicate());
			stWhere.byEntity(entity, columnPredicate);
		}

		if (where.relation() == Relation.OR) {
			stWhere = stWhere.or();
		}
		for (io.polaris.core.jdbc.sql.annotation.segment.Criteria criteria : where.criteria()) {
			addWhereByCriteria(cache, bindings, criteria, stWhere);
		}
	}

	private static void addJoinGroupByClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, JoinSegment<?, ?> st
		, GroupBy[] groupBys) {
		if (groupBys != null && groupBys.length > 0) {
			for (GroupBy groupBy : groupBys) {
				String raw = groupBy.raw();
				String field = groupBy.field();
				if (Strings.isNotBlank(raw)) {
					st.groupBy().rawColumn(raw);
				} else if (Strings.isNotBlank(field)) {
					st.groupBy().column(field);
				}
			}
		}
	}

	private static void addJoinHavingClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, JoinSegment<?, ?> st, Having having) {
		io.polaris.core.jdbc.sql.annotation.segment.Criteria[] havingCriteria = having.criteria();
		Relation havingRelation = having.relation();
		if (havingCriteria != null && havingCriteria.length > 0) {
			WhereSegment<?, ?> ws = st.having();
			if (havingRelation == Relation.OR) {
				ws = ws.or();
			}
			for (io.polaris.core.jdbc.sql.annotation.segment.Criteria criteria : havingCriteria) {
				addWhereByCriteria(cache, bindings, criteria, ws);
			}
		}
	}

	private static void addJoinOrderByClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, JoinSegment<?, ?> st, Join sqlSelect) {
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
		}
	}

	private static void addWhereClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		WhereSegment<?, ?> ws, Where where,
		io.polaris.core.jdbc.sql.annotation.segment.ColumnPredicate columnedPredicate) {
		String entityIdKey = where.byEntityIdKey();
		String entityKey = where.byEntityKey();
		if (Strings.isNotBlank(entityIdKey)) {
			// 存在主键条件时
			Object entity = BindingValues.getBindingValueOrDefault(cache, bindings, entityIdKey, Collections.emptyMap());
			ws.byEntityId(entity);
		} else if (Strings.isNotBlank(entityKey)) {
			// 不存在主键条件时，使用实体全字段条件
			Object entity = BindingValues.getBindingValueOrDefault(cache, bindings, entityKey, Collections.emptyMap());
			ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
				columnedPredicate);
			ws.byEntity(entity, columnPredicate);
		}

		if (where.relation() == Relation.OR) {
			ws = ws.or();
		}
		for (io.polaris.core.jdbc.sql.annotation.segment.Criteria criteria : where.criteria()) {
			addWhereByCriteria(cache, bindings, criteria, ws);
		}
	}

	private static void addGroupByClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, SelectStatement<?> st
		, GroupBy[] groupBys) {
		if (groupBys != null && groupBys.length > 0) {
			for (GroupBy groupBy : groupBys) {
				String raw = groupBy.raw();
				String field = groupBy.field();
				if (Strings.isNotBlank(raw)) {
					st.groupBy().rawColumn(raw);
				} else if (Strings.isNotBlank(field)) {
					st.groupBy().column(field);
				}
			}
		}
	}

	private static void addHavingClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, SelectStatement<?> st, Having having) {
		io.polaris.core.jdbc.sql.annotation.segment.Criteria[] havingCriteria = having.criteria();
		Relation havingRelation = having.relation();
		if (havingCriteria != null && havingCriteria.length > 0) {
			WhereSegment<?, ?> ws = st.having();
			if (havingRelation == Relation.OR) {
				ws = ws.or();
			}
			for (io.polaris.core.jdbc.sql.annotation.segment.Criteria criteria : havingCriteria) {
				addWhereByCriteria(cache, bindings, criteria, ws);
			}
		}
	}

	private static void addOrderByClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, SelectStatement<?> st, SqlSelect sqlSelect) {
		boolean hasOrderByKey = false;
		String orderByKey = sqlSelect.orderByKey();
		if (Strings.isNotBlank(orderByKey)) {
			Object orderByObj = BindingValues.getBindingValueOrDefault(cache, bindings, orderByKey, null);
			OrderBy orderBy = null;
			if (orderByObj instanceof String) {
				orderBy = Queries.newOrderBy((String) orderByObj);
			} else if (orderByObj instanceof OrderBy) {
				orderBy = (OrderBy) orderByObj;
			}
			if (orderBy != null) {
				st.orderBy(orderBy);
				hasOrderByKey = true;
			}
		}
		if (!hasOrderByKey) {
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
			}
		}
	}


	private static CriterionSegment<?, ?> newCriterionSegmentWithFunction(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, WhereSegment<?, ?> ws, Criterion criterion) {
		CriterionSegment<?, ?> seg = ws.column(criterion.field());
		if (criterion.count()) {
			seg = seg.count();
		}
		if (criterion.sum()) {
			seg = seg.sum();
		}
		if (criterion.max()) {
			seg = seg.max();
		}
		if (criterion.min()) {
			seg = seg.min();
		}
		if (criterion.avg()) {
			seg = seg.avg();
		}
		labelFunc:
		for (io.polaris.core.jdbc.sql.annotation.segment.Function function : criterion.functions()) {
			String expr = function.value();
			if (Strings.isBlank(expr)) {
				continue;
			}
			JoinColumn[] joinColumns = function.joinColumns();
			TableField[] tableFields = new TableField[joinColumns.length];
			for (int i = 0; i < tableFields.length; i++) {
				TableField v = getJoinTableField(cache, bindings, joinColumns[i]);
				if (v == null) {
					// 条件不满足
					continue labelFunc;
				}
			}
			BindingKey[] bindingKeys = function.bindingKeys();
			if (bindingKeys.length > 0) {
				Object[] args = new Object[bindingKeys.length];
				for (int i = 0; i < bindingKeys.length; i++) {
					Tuple1<?> val = getValForBindingKey(cache, bindings, bindingKeys[i]);
					if (val == null) {
						// 条件不满足
						continue labelFunc;
					}
					args[i] = val.getFirst();
				}
				seg = seg.apply(expr, tableFields, args);
			} else {
				seg = seg.apply(expr, tableFields, bindings);
			}
		}

		return seg;
	}

	private static CriterionSegment<?, ?> newSubCriterionSegmentWithFunction(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, WhereSegment<?, ?> ws, SubCriterion criterion) {
		CriterionSegment<?, ?> seg = ws.column(criterion.field());
		if (criterion.count()) {
			seg = seg.count();
		}
		if (criterion.sum()) {
			seg = seg.sum();
		}
		if (criterion.max()) {
			seg = seg.max();
		}
		if (criterion.min()) {
			seg = seg.min();
		}
		if (criterion.avg()) {
			seg = seg.avg();
		}
		labelFunc:
		for (io.polaris.core.jdbc.sql.annotation.segment.Function function : criterion.functions()) {
			String expr = function.value();
			if (Strings.isBlank(expr)) {
				continue;
			}
			JoinColumn[] joinColumns = function.joinColumns();
			TableField[] tableFields = new TableField[joinColumns.length];
			for (int i = 0; i < tableFields.length; i++) {
				TableField v = getJoinTableField(cache, bindings, joinColumns[i]);
				if (v == null) {
					// 条件不满足
					continue labelFunc;
				}
			}
			BindingKey[] bindingKeys = function.bindingKeys();
			if (bindingKeys.length > 0) {
				Object[] args = new Object[bindingKeys.length];
				for (int i = 0; i < bindingKeys.length; i++) {
					Tuple1<?> val = getValForBindingKey(cache, bindings, bindingKeys[i]);
					if (val == null) {
						// 条件不满足
						continue labelFunc;
					}
					args[i] = val.getFirst();
				}
				seg = seg.apply(expr, tableFields, args);
			} else {
				seg = seg.apply(expr, tableFields, bindings);
			}
		}
		return seg;
	}

	private static CriterionSegment<?, ?> newJoinCriterionSegmentWithFunction(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, WhereSegment<?, ?> ws, JoinCriterion criterion) {
		CriterionSegment<?, ?> seg = ws.column(criterion.field());
		if (criterion.count()) {
			seg = seg.count();
		}
		if (criterion.sum()) {
			seg = seg.sum();
		}
		if (criterion.max()) {
			seg = seg.max();
		}
		if (criterion.min()) {
			seg = seg.min();
		}
		if (criterion.avg()) {
			seg = seg.avg();
		}
		labelFunc:
		for (io.polaris.core.jdbc.sql.annotation.segment.Function function : criterion.functions()) {
			String expr = function.value();
			if (Strings.isBlank(expr)) {
				continue;
			}
			JoinColumn[] joinColumns = function.joinColumns();
			TableField[] tableFields = new TableField[joinColumns.length];
			for (int i = 0; i < tableFields.length; i++) {
				TableField v = getJoinTableField(cache, bindings, joinColumns[i]);
				if (v == null) {
					// 条件不满足
					continue labelFunc;
				}
			}
			BindingKey[] bindingKeys = function.bindingKeys();
			if (bindingKeys.length > 0) {
				Object[] args = new Object[bindingKeys.length];
				for (int i = 0; i < bindingKeys.length; i++) {
					Tuple1<?> val = getValForBindingKey(cache, bindings, bindingKeys[i]);
					if (val == null) {
						// 条件不满足
						continue labelFunc;
					}
					args[i] = val.getFirst();
				}
				seg = seg.apply(expr, tableFields, args);
			} else {
				seg = seg.apply(expr, tableFields, bindings);
			}
		}
		return seg;
	}

	private static void addWhereByCriteria(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		io.polaris.core.jdbc.sql.annotation.segment.Criteria criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (Criterion criterion : criteria.value()) {
			addWhereByCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
		for (Criteria1 sub : criteria.subset()) {
			addWhereByCriteria1(cache, bindings, sub, ws);
		}
	}

	private static void addWhereByCriteria1(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		Criteria1 criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (Criterion criterion : criteria.value()) {
			addWhereByCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
		for (Criteria2 sub : criteria.subset()) {
			addWhereByCriteria2(cache, bindings, sub, ws);
		}
	}

	private static void addWhereByCriteria2(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		Criteria2 criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (Criterion criterion : criteria.value()) {
			addWhereByCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
		for (Criteria3 sub : criteria.subset()) {
			addWhereByCriteria3(cache, bindings, sub, ws);
		}
	}

	private static void addWhereByCriteria3(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		Criteria3 criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (Criterion criterion : criteria.value()) {
			addWhereByCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
		for (Criteria4 sub : criteria.subset()) {
			addWhereByCriteria4(cache, bindings, sub, ws);
		}
	}

	private static void addWhereByCriteria4(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		Criteria4 criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (Criterion criterion : criteria.value()) {
			addWhereByCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
		for (Criteria5 sub : criteria.subset()) {
			addWhereByCriteria5(cache, bindings, sub, ws);
		}
	}

	private static void addWhereByCriteria5(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		Criteria5 criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (Criterion criterion : criteria.value()) {
			addWhereByCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
	}


	private static void addWhereByCriterion(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, WhereSegment<?, ?> ws, Criterion criterion) {
		String raw = criterion.raw();
		if (Strings.isNotBlank(raw)) {
			ws.raw(raw);
			return;
		}
		String field = criterion.field();
		if (Strings.isBlank(field)) {
			return;
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.eq());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).eq(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.ne());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).ne(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.gt());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).gt(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.ge());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).ge(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.lt());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).lt(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.le());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).le(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.isNull());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).isNull();
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notNull());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).notNull();
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.contains());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).contains(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notContains());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).notContains(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.startsWith());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).startsWith(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notStartsWith());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).notStartsWith(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.endsWith());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).endsWith(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notEndsWith());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).notEndsWith(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.like());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).like(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notLike());
			if (val != null) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).notLike(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			BindingKey[] between = criterion.between();
			if (between.length >= 2) {
				Tuple1<?> val0 = getValForBindingKey(cache, bindings, between[0]);
				Tuple1<?> val1 = getValForBindingKey(cache, bindings, between[1]);
				if (val1 != null && val1 != null) {
					newCriterionSegmentWithFunction(cache, bindings, ws, criterion).between(val0, val1);
				}
			}
		}
		{
			BindingKey[] notBetween = criterion.notBetween();
			if (notBetween.length >= 2) {
				Tuple1<?> val0 = getValForBindingKey(cache, bindings, notBetween[0]);
				Tuple1<?> val1 = getValForBindingKey(cache, bindings, notBetween[1]);
				if (val1 != null && val1 != null) {
					newCriterionSegmentWithFunction(cache, bindings, ws, criterion).notBetween(val0, val1);
				}
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.in());
			if (val != null && val.getFirst() instanceof Collection) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).in((Collection) val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notIn());
			if (val != null && val.getFirst() instanceof Collection) {
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).notIn((Collection) val.getFirst());
			}
		}
		{
			SubSelect subSelect = criterion.exists();
			String tableAlias = subSelect.alias();
			Class<?> entityClass = subSelect.table();
			if (entityClass != void.class && Strings.isNotBlank(tableAlias)) {
				SelectStatement<?> st = new SelectStatement<>(entityClass, tableAlias);
				// select
				addSelectColumns(cache, bindings, st, subSelect.columns(), subSelect.quotaSelectAlias());
				ws.exists(st);
				// where
				addSubWhereClause(cache, bindings, st, subSelect);
				// group by
				addGroupByClause(cache, bindings, st, subSelect.groupBy());
				// having
				addSubHavingClause(cache, bindings, st, subSelect.having());
			}
		}
		{
			SubSelect subSelect = criterion.notExists();
			String tableAlias = subSelect.alias();
			Class<?> entityClass = subSelect.table();
			if (entityClass != void.class && Strings.isNotBlank(tableAlias)) {
				SelectStatement<?> st = new SelectStatement<>(entityClass, tableAlias);
				// select
				addSelectColumns(cache, bindings, st, subSelect.columns(), subSelect.quotaSelectAlias());
				ws.notExists(st);
				// where
				addSubWhereClause(cache, bindings, st, subSelect);
				// group by
				addGroupByClause(cache, bindings, st, subSelect.groupBy());
				// having
				addSubHavingClause(cache, bindings, st, subSelect.having());
			}
		}
		{
			SubSelect subSelect = criterion.inSubSelect();
			String tableAlias = subSelect.alias();
			Class<?> entityClass = subSelect.table();
			if (entityClass != void.class && Strings.isNotBlank(tableAlias)) {
				SelectStatement<?> st = new SelectStatement<>(entityClass, tableAlias);
				// select
				addSelectColumns(cache, bindings, st, subSelect.columns(), subSelect.quotaSelectAlias());
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).in(st);
				// where
				addSubWhereClause(cache, bindings, st, subSelect);
				// group by
				addGroupByClause(cache, bindings, st, subSelect.groupBy());
				// having
				addSubHavingClause(cache, bindings, st, subSelect.having());
			}
		}
		{
			SubSelect subSelect = criterion.notInSubSelect();
			String tableAlias = subSelect.alias();
			Class<?> entityClass = subSelect.table();
			if (entityClass != void.class && Strings.isNotBlank(tableAlias)) {
				SelectStatement<?> st = new SelectStatement<>(entityClass, tableAlias);
				// select
				addSelectColumns(cache, bindings, st, subSelect.columns(), subSelect.quotaSelectAlias());
				newCriterionSegmentWithFunction(cache, bindings, ws, criterion).notIn(st);
				// where
				addSubWhereClause(cache, bindings, st, subSelect);
				// group by
				addGroupByClause(cache, bindings, st, subSelect.groupBy());
				// having
				addSubHavingClause(cache, bindings, st, subSelect.having());
			}
		}
	}

	private static void addSubHavingClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, SelectStatement<?> st, SubHaving having) {
		SubCriteria[] havingCriteria = having.criteria();
		Relation havingRelation = having.relation();
		if (havingCriteria != null && havingCriteria.length > 0) {
			WhereSegment<?, ?> ws = st.having();
			if (havingRelation == Relation.OR) {
				ws = ws.or();
			}
			for (SubCriteria criteria : havingCriteria) {
				addSubWhereByCriteria(cache, bindings, criteria, ws);
			}
		}
	}

	private static void addWhereByJoinCriterion(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, WhereSegment<?, ?> ws, JoinCriterion criterion) {
		String raw = criterion.raw();
		if (Strings.isNotBlank(raw)) {
			ws.raw(raw);
			return;
		}
		String field = criterion.field();
		if (Strings.isBlank(field)) {
			return;
		}
		{
			TableField v = getJoinTableField(cache, bindings, criterion.eq());
			if (v != null) {
				newJoinCriterionSegmentWithFunction(cache, bindings, ws, criterion).eq(v);
			}
		}
		{
			TableField v = getJoinTableField(cache, bindings, criterion.ne());
			if (v != null) {
				newJoinCriterionSegmentWithFunction(cache, bindings, ws, criterion).ne(v);
			}
		}
		{
			TableField v = getJoinTableField(cache, bindings, criterion.gt());
			if (v != null) {
				newJoinCriterionSegmentWithFunction(cache, bindings, ws, criterion).gt(v);
			}
		}
		{
			TableField v = getJoinTableField(cache, bindings, criterion.ge());
			if (v != null) {
				newJoinCriterionSegmentWithFunction(cache, bindings, ws, criterion).ge(v);
			}
		}
		{
			TableField v = getJoinTableField(cache, bindings, criterion.lt());
			if (v != null) {
				newJoinCriterionSegmentWithFunction(cache, bindings, ws, criterion).lt(v);
			}
		}
		{
			TableField v = getJoinTableField(cache, bindings, criterion.le());
			if (v != null) {
				newJoinCriterionSegmentWithFunction(cache, bindings, ws, criterion).le(v);
			}
		}
		{
			JoinColumn[] between = criterion.between();
			if (between.length >= 2) {
				TableField val0 = getJoinTableField(cache, bindings, between[0]);
				TableField val1 = getJoinTableField(cache, bindings, between[1]);
				if (val1 != null && val1 != null) {
					newJoinCriterionSegmentWithFunction(cache, bindings, ws, criterion).between(val0, val1);
				}
			}
		}
		{
			JoinColumn[] notBetween = criterion.notBetween();
			if (notBetween.length >= 2) {
				TableField val0 = getJoinTableField(cache, bindings, notBetween[0]);
				TableField val1 = getJoinTableField(cache, bindings, notBetween[1]);
				if (val1 != null && val1 != null) {
					newJoinCriterionSegmentWithFunction(cache, bindings, ws, criterion).notBetween(val0, val1);
				}
			}
		}

	}

	private static void addSubWhereClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, SelectStatement<?> st
		, SubSelect subSelect) {
		SubWhere where = subSelect.where();
		String entityIdKey = where.byEntityIdKey();
		String entityKey = where.byEntityKey();
		if (Strings.isNotBlank(entityIdKey)) {
			// 存在主键条件时
			Object entity = BindingValues.getBindingValueOrDefault(cache, bindings, entityIdKey, Collections.emptyMap());
			st.where().byEntityId(entity);
		}
		if (Strings.isNotBlank(entityKey)) {
			// 不存在主键条件时，使用实体全字段条件
			Object entity = BindingValues.getBindingValueOrDefault(cache, bindings, entityKey, Collections.emptyMap());
			ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings, subSelect.columnPredicate());
			st.where().byEntity(entity, columnPredicate);
		}
		WhereSegment<?, ?> stWhere = st.where();

		if (where.relation() == Relation.OR) {
			stWhere = st.where().or();
		}
		for (SubCriteria criteria : where.criteria()) {
			addSubWhereByCriteria(cache, bindings, criteria, stWhere);
		}
	}

	private static void addSubWhereByCriteria(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		SubCriteria criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (SubCriterion criterion : criteria.value()) {
			addWhereBySubCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
		for (SubCriteria1 sub : criteria.subset()) {
			addSubWhereByCriteria1(cache, bindings, sub, ws);
		}
	}

	private static void addSubWhereByCriteria1(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		SubCriteria1 criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (SubCriterion criterion : criteria.value()) {
			addWhereBySubCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
		for (SubCriteria2 sub : criteria.subset()) {
			addSubWhereByCriteria2(cache, bindings, sub, ws);
		}
	}

	private static void addSubWhereByCriteria2(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		SubCriteria2 criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (SubCriterion criterion : criteria.value()) {
			addWhereBySubCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
		for (SubCriteria3 sub : criteria.subset()) {
			addSubWhereByCriteria3(cache, bindings, sub, ws);
		}
	}

	private static void addSubWhereByCriteria3(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		SubCriteria3 criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (SubCriterion criterion : criteria.value()) {
			addWhereBySubCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
		for (SubCriteria4 sub : criteria.subset()) {
			addSubWhereByCriteria4(cache, bindings, sub, ws);
		}
	}

	private static void addSubWhereByCriteria4(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		SubCriteria4 criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (SubCriterion criterion : criteria.value()) {
			addWhereBySubCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
		for (SubCriteria5 sub : criteria.subset()) {
			addSubWhereByCriteria5(cache, bindings, sub, ws);
		}
	}

	private static void addSubWhereByCriteria5(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		SubCriteria5 criteria, WhereSegment<?, ?> ws) {
		if (criteria.relation() == Relation.OR) {
			ws = ws.or();
		} else {
			ws = ws.and();
		}
		for (SubCriterion criterion : criteria.value()) {
			addWhereBySubCriterion(cache, bindings, ws, criterion);
		}
		for (JoinCriterion joinCriterion : criteria.join()) {
			addWhereByJoinCriterion(cache, bindings, ws, joinCriterion);
		}
	}

	private static void addWhereBySubCriterion(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, WhereSegment<?, ?> ws, SubCriterion criterion) {
		String raw = criterion.raw();
		if (Strings.isNotBlank(raw)) {
			ws.raw(raw);
			return;
		}
		String field = criterion.field();
		if (Strings.isBlank(field)) {
			return;
		}

		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.eq());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).eq(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.ne());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).ne(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.gt());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).gt(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.ge());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).ge(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.lt());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).lt(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.le());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).le(val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.isNull());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).isNull();
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notNull());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).notNull();
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.contains());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).contains(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notContains());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).notContains(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.startsWith());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).startsWith(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notStartsWith());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).notStartsWith(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.endsWith());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).endsWith(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notEndsWith());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).notEndsWith(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.like());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).like(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notLike());
			if (val != null) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).notLike(Converters.convertQuietly(String.class, val.getFirst()));
			}
		}
		{
			BindingKey[] between = criterion.between();
			if (between.length >= 2) {
				Tuple1<?> val0 = getValForBindingKey(cache, bindings, between[0]);
				Tuple1<?> val1 = getValForBindingKey(cache, bindings, between[1]);
				if (val1 != null && val1 != null) {
					newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).between(val0, val1);
				}
			}
		}
		{
			BindingKey[] notBetween = criterion.notBetween();
			if (notBetween.length >= 2) {
				Tuple1<?> val0 = getValForBindingKey(cache, bindings, notBetween[0]);
				Tuple1<?> val1 = getValForBindingKey(cache, bindings, notBetween[1]);
				if (val1 != null && val1 != null) {
					newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).notBetween(val0, val1);
				}
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.in());
			if (val != null && val.getFirst() instanceof Collection) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).in((Collection) val.getFirst());
			}
		}
		{
			Tuple1<?> val = getValForBindingKey(cache, bindings, criterion.notIn());
			if (val != null && val.getFirst() instanceof Collection) {
				newSubCriterionSegmentWithFunction(cache, bindings, ws, criterion).notIn((Collection) val.getFirst());
			}
		}
	}

	private static TableField getJoinTableField(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, JoinColumn joinColumn) {
		String field = joinColumn.tableField();
		String tableAlias = joinColumn.tableAlias();
		if (Strings.isBlank(field) || Strings.isBlank(tableAlias)) {
			return null;
		}
		if (!evalConditionPredicate(cache, bindings, null, joinColumn.condition())) {
			return null;
		}
		return TableField.of(tableAlias, field);
	}

	private static Tuple1<?> getValForBindingKey(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, BindingKey bindingKey) {
		String key = bindingKey.value();
		if (Strings.isBlank(key)) {
			return null;
		}
		if (!evalConditionPredicate(cache, bindings, key, bindingKey.condition())) {
			return null;
		}
		return Tuple1.of(BindingValues.getBindingValueOrDefault(cache, bindings, key, null));
	}


	private static boolean evalConditionPredicate(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, String bindingKey, Condition[] conditions) {
		for (Condition condition : conditions) {
			String key = Strings.coalesce(condition.bindingKey(), bindingKey);
			if (Strings.isBlank(key)) {
				return false;
			}
			Object condVal = BindingValues.getBindingValueOrDefault(cache, bindings, key, null);
			Condition.PredicateType predicateType = condition.predicateType();
			switch (predicateType) {
				case NOT_NULL: {
					if (condVal == null) {
						return false;
					}
					break;
				}
				case NOT_EMPTY: {
					if (Objs.isEmpty(condVal)) {
						return false;
					}
					break;
				}
				case IS_NULL: {
					if (condVal != null) {
						return false;
					}
					break;
				}
				case IS_EMPTY: {
					if (Objs.isNotEmpty(condVal)) {
						return false;
					}
					break;
				}
				case REGEX: {
					String expression = condition.predicateExpression();
					if (Strings.isBlank(expression)) {
						return false;
					}
					if (!(condVal instanceof String)) {
						return false;
					}
					if (!Patterns.matches(expression, (String) condVal)) {
						return false;
					}
					break;
				}
				case GROOVY_EVALUATOR: {
					boolean rs = evalConditionPredicateByEngineName(GroovyEvaluator.ENGINE_NAME, condition.predicateExpression(), condVal, bindings);
					if (!rs) {
						return false;
					}
					break;
				}
				case JAVA_EVALUATOR: {
					boolean rs = evalConditionPredicateByEngineName(JavaEvaluator.ENGINE_NAME, condition.predicateExpression(), condVal, bindings);
					if (!rs) {
						return false;
					}
					break;
				}
				case JAVASCRIPT_EVALUATOR: {
					boolean rs = evalConditionPredicateByEngineName(JavaScriptEvaluator.ENGINE_NAME, condition.predicateExpression(), condVal, bindings);
					if (!rs) {
						return false;
					}
					break;
				}
				case CUSTOM: {
					String predicateKey = condition.predicateKey();
					if (Strings.isNotBlank(predicateKey)) {
						BiPredicate<Map<String, Object>, Object> predicate = (BiPredicate<Map<String, Object>, Object>) BindingValues.getBindingValueOrDefault(cache, bindings, predicateKey, null);
						if (predicate == null || !predicate.test(bindings, condVal)) {
							return false;
						}
					} else {
						for (Class<? extends BiPredicate<Map<String, Object>, Object>> c : condition.predicateClass()) {
							try {
								BiPredicate<Map<String, Object>, Object> biPredicate = c.newInstance();
								if (!biPredicate.test(bindings, condVal)) {
									return false;
								}
							} catch (ReflectiveOperationException e) {
								return false;
							}
						}
					}
					break;
				}
				default:
			}
		}
		return true;
	}

	private static boolean evalConditionPredicateByEngineName(String engineName, String expression, Object condVal, Map<String, Object> bindings) {
		if (Strings.isBlank(expression)) {
			return false;
		}
		Evaluator evaluator = ScriptEvaluators.getEvaluator(engineName);
		Map<String, Object> output = new HashMap<>();
		expression = Evaluator.OUTPUT + "put(\"" + Evaluator.RESULT + "\",(" + expression + "));";
		evaluator.eval(expression, condVal, output, bindings);
		Object o = output.get(Evaluator.RESULT);
		return Converters.convertQuietly(boolean.class, o, false);
	}


	public static InsertStatement<?> buildInsert(Map<String, Object> bindings, EntityInsert entityInsert) {
		return buildInsert(bindings, entityInsert.table(), entityInsert.entityKey()
			, entityInsert.enableReplace(), entityInsert.enableUpdateByDuplicateKey()
			, entityInsert.columnPredicate());
	}

	public static InsertStatement<?> buildInsert(Map<String, Object> bindings, Class<?> entityClass, String entityKey,
		boolean enableReplace, boolean enableUpdateByDuplicateKey,
		io.polaris.core.jdbc.sql.annotation.segment.ColumnPredicate predicate
	) {

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings, predicate);
		return buildInsert(bindings, entityClass, entityKey, enableReplace, enableUpdateByDuplicateKey, columnPredicate);
	}

	public static InsertStatement<?> buildInsert(Map<String, Object> bindings, Class<?> entityClass, String entityKey, boolean enableReplace, boolean enableUpdateByDuplicateKey, ColumnPredicate columnPredicate) {
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, Collections.emptyMap());
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
		return buildDelete(bindings, entityDelete.table()
			, Strings.trimToNull(entityDelete.alias())
			, entityDelete.byId(), entityDelete.entityKey(), entityDelete.whereKey()
			, entityDelete.columnPredicate());
	}

	public static DeleteStatement<?> buildDelete(Map<String, Object> bindings, Class<?> entityClass, String tableAlias,
		boolean byId, String entityKey, String whereKey,
		io.polaris.core.jdbc.sql.annotation.segment.ColumnPredicate predicate
	) {
		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings, predicate);
		return buildDelete(bindings, entityClass, tableAlias, byId, entityKey, whereKey, columnPredicate);
	}

	public static DeleteStatement<?> buildDelete(Map<String, Object> bindings, Class<?> entityClass, String tableAlias, boolean byId, String entityKey, String whereKey, ColumnPredicate columnPredicate) {
		DeleteStatement<?> st = new DeleteStatement<>(entityClass, Strings.coalesce(tableAlias, DEFAULT_TABLE_ALIAS));
		if (byId) {
			Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
			if (entity == null) {
				entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
			}
			st.where().byEntityId(entity);
		} else {
			Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
			if (entity != null) {
				if (entity instanceof Criteria) {
					st.where((Criteria) entity);
				} else {
					st.where().byEntity(entity, columnPredicate);
				}
			}
			entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, null);
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
		return buildUpdate(bindings, entityUpdate.table()
			, Strings.trimToNull(entityUpdate.alias())
			, entityUpdate.byId(), entityUpdate.entityKey(), entityUpdate.whereKey()
			, entityUpdate.columnPredicate(), entityUpdate.whereColumnPredicate()
		);
	}

	public static UpdateStatement<?> buildUpdate(Map<String, Object> bindings, Class<?> entityClass, String tableAlias,
		boolean byId, String entityKey, String whereKey,
		io.polaris.core.jdbc.sql.annotation.segment.ColumnPredicate predicate,
		io.polaris.core.jdbc.sql.annotation.segment.ColumnPredicate wherePredicate
	) {
		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings, predicate);
		ColumnPredicate whereColumnPredicate = ConfigurableColumnPredicate.of(bindings, wherePredicate);
		return buildUpdate(bindings, entityClass, tableAlias, byId, entityKey, whereKey, columnPredicate, whereColumnPredicate);
	}

	public static UpdateStatement<?> buildUpdate(Map<String, Object> bindings, Class<?> entityClass, String tableAlias, boolean byId, String entityKey, String whereKey, ColumnPredicate columnPredicate, ColumnPredicate whereColumnPredicate) {
		UpdateStatement<?> st = new UpdateStatement<>(entityClass, Strings.coalesce(tableAlias, DEFAULT_TABLE_ALIAS));
		if (byId) {
			Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
			if (entity == null) {
				entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
			}
			st.withEntity(entity, columnPredicate);
			st.where().byEntityId(entity);
			return st;
		} else {
			Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, Collections.emptyMap());
			Object where = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());

			st.withEntity(entity, columnPredicate);

			if (where instanceof Criteria) {
				st.where((Criteria) where);
			} else {
				st.where().byEntity(where, whereColumnPredicate);
			}
		}
		return st;
	}


	public static SelectStatement<?> buildSelect(Map<String, Object> bindings, EntitySelect entitySelect) {
		return buildSelect(bindings, entitySelect.table()
			, Strings.trimToNull(entitySelect.alias())
			, entitySelect.byId(), entitySelect.entityKey()
			, entitySelect.whereKey(), entitySelect.orderByKey()
			, entitySelect.columnPredicate());
	}

	public static SelectStatement<?> buildSelect(Map<String, Object> bindings, Class<?> entityClass, String tableAlias,
		boolean byId, String entityKey, String whereKey, String orderByKey,
		io.polaris.core.jdbc.sql.annotation.segment.ColumnPredicate predicate
	) {
		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings, predicate);
		return buildSelect(bindings, entityClass, tableAlias, byId, entityKey, whereKey, orderByKey, columnPredicate);
	}

	public static SelectStatement<?> buildSelect(Map<String, Object> bindings, Class<?> entityClass, String tableAlias, boolean byId, String entityKey, String whereKey, String orderByKey, ColumnPredicate columnPredicate) {
		SelectStatement<?> st = new SelectStatement<>(entityClass, Strings.coalesce(tableAlias, DEFAULT_TABLE_ALIAS));
		st.selectAll();

		// binding-cache
		Map<String, ValueRef<Object>> cache = new HashMap<>();
		if (byId) {
			Object entity = BindingValues.getBindingValueOrDefault(cache, bindings, entityKey, null);
			if (entity == null) {
				entity = BindingValues.getBindingValueOrDefault(cache, bindings, whereKey, Collections.emptyMap());
			}
			st.where().byEntityId(entity);
		} else {
			Object entity = BindingValues.getBindingValueOrDefault(cache, bindings, entityKey, null);
			if (entity != null) {
				if (entity instanceof Criteria) {
					st.where((Criteria) entity);
				} else {
					st.where().byEntity(entity, columnPredicate);
				}
			}
			entity = BindingValues.getBindingValueOrDefault(cache, bindings, whereKey, null);
			if (entity != null) {
				if (entity instanceof Criteria) {
					st.where((Criteria) entity);
				} else {
					st.where().byEntity(entity, columnPredicate);
				}
			}
		}

		// 排序字段
		Object orderByObj = BindingValues.getBindingValueOrDefault(cache, bindings, orderByKey, null);
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
		return buildMerge(bindings, entityMerge.table()
			, Strings.trimToNull(entityMerge.alias())
			, entityMerge.entityKey(), entityMerge.updateWhenMatched(), entityMerge.insertWhenNotMatched()
			, entityMerge.columnPredicate());
	}

	public static MergeStatement<?> buildMerge(Map<String, Object> bindings, Class<?> entityClass,
		String tableAlias, String entityKey,
		boolean updateWhenMatched, boolean insertWhenNotMatched,
		io.polaris.core.jdbc.sql.annotation.segment.ColumnPredicate predicate
	) {
		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings, predicate);
		return buildMerge(bindings, entityClass, tableAlias, entityKey, updateWhenMatched, insertWhenNotMatched, columnPredicate);
	}

	public static MergeStatement<?> buildMerge(Map<String, Object> bindings, Class<?> entityClass, String tableAlias, String entityKey, boolean updateWhenMatched, boolean insertWhenNotMatched, ColumnPredicate columnPredicate) {
		MergeStatement<?> st = new MergeStatement<>(entityClass, Strings.coalesce(tableAlias, DEFAULT_TABLE_ALIAS));
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, Collections.emptyMap());
		st.withEntity(entity, updateWhenMatched, insertWhenNotMatched, columnPredicate);
		return st;
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

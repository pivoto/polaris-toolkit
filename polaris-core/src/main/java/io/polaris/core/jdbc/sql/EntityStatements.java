package io.polaris.core.jdbc.sql;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.annotation.*;
import io.polaris.core.jdbc.annotation.segment.*;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.consts.JoinType;
import io.polaris.core.jdbc.sql.consts.Relation;
import io.polaris.core.jdbc.sql.consts.SelectSetOps;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
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
import io.polaris.core.jdbc.sql.statement.segment.OrderBySegment;
import io.polaris.core.jdbc.sql.statement.segment.SelectSegment;
import io.polaris.core.jdbc.sql.statement.segment.TableAccessible;
import io.polaris.core.jdbc.sql.statement.segment.TableField;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;
import io.polaris.core.jdbc.sql.statement.segment.WhereSegment;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.annotation.AnnotationAttributes;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.regex.Patterns;
import io.polaris.core.script.Evaluator;
import io.polaris.core.script.ScriptEvaluators;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Tuple1;
import io.polaris.core.tuple.Tuple3;
import io.polaris.core.tuple.ValueRef;

/**
 * @author Qt
 * @since Jan 27, 2024
 */
@SuppressWarnings({"all"})
public class EntityStatements {

	public static final String DEFAULT_TABLE_ALIAS = "T";

	public static TableMeta getTableMeta(String entityClassName) {
		return TableMetaKit.instance().get(entityClassName);
	}

	public static TableAccessible asTableAccessible(SqlEntity sqlEntityDeclared) {
		TableAccessible tableAccessible = null;
		if (sqlEntityDeclared != null) {
			Class<?>[] tables = sqlEntityDeclared.table();
			String[] aliases = sqlEntityDeclared.alias();
			int len = Integer.min(tables.length, aliases.length);
			if (len > 0) {
				TableSegment<?>[] tableSegments = new TableSegment<?>[len];
				for (int i = 0; i < len; i++) {
					tableSegments[i] = TableSegment.fromEntity(tables[i], aliases[i]);
				}
				tableAccessible = TableAccessible.of(tableSegments);
			}
		}
		return tableAccessible;
	}

	public static java.util.function.Function<Map<String, Object>, SqlNode> buildSqlUpdateFunction(Method method) {
		{
			EntityInsert entityInsert = method.getAnnotation(EntityInsert.class);
			if (entityInsert != null) {
				return (bindings) -> EntityStatements.buildInsert(bindings, entityInsert).toSqlNode();
			}
		}
		{
			EntityDelete entityDelete = method.getAnnotation(EntityDelete.class);
			if (entityDelete != null) {
				return (bindings) -> EntityStatements.buildDelete(bindings, entityDelete).toSqlNode();
			}
		}
		{
			EntityUpdate entityUpdate = method.getAnnotation(EntityUpdate.class);
			if (entityUpdate != null) {
				return (bindings) -> EntityStatements.buildUpdate(bindings, entityUpdate).toSqlNode();
			}
		}
		{
			EntityMerge entityMerge = method.getAnnotation(EntityMerge.class);
			if (entityMerge != null) {
				return (bindings) -> EntityStatements.buildMerge(bindings, entityMerge).toSqlNode();
			}
		}
		{
			SqlInsert sqlInsert = method.getAnnotation(SqlInsert.class);
			if (sqlInsert != null) {
				return (bindings) -> EntityStatements.buildInsert(bindings, sqlInsert).toSqlNode();
			}
		}
		{
			SqlDelete sqlDelete = method.getAnnotation(SqlDelete.class);
			if (sqlDelete != null) {
				return (bindings) -> EntityStatements.buildDelete(bindings, sqlDelete).toSqlNode();
			}
		}
		{
			SqlUpdate sqlUpdate = method.getAnnotation(SqlUpdate.class);
			if (sqlUpdate != null) {
				return (bindings) -> EntityStatements.buildUpdate(bindings, sqlUpdate).toSqlNode();
			}
		}
		return buildSqlRawFunction(method);
	}

	public static java.util.function.Function<Map<String, Object>, SqlNode> buildSqlSelectFunction(Method method) {
		{
			EntitySelect entitySelect = method.getAnnotation(EntitySelect.class);
			if (entitySelect != null) {
				if (entitySelect.count()) {
					return (bindings) -> EntityStatements.buildSelect(bindings, entitySelect).toCountSqlNode();
				}
				return (bindings) -> EntityStatements.buildSelect(bindings, entitySelect).toSqlNode();
			}
		}
		{
			SqlSelect sqlSelect = method.getAnnotation(SqlSelect.class);
			if (sqlSelect != null) {
				if (sqlSelect.count()) {
					return (bindings) -> EntityStatements.buildSelect(bindings, sqlSelect).toCountSqlNode();
				}
				return (bindings) -> EntityStatements.buildSelect(bindings, sqlSelect).toSqlNode();
			}
		}
		{
			SqlSelectSet sqlSelect = method.getAnnotation(SqlSelectSet.class);
			if (sqlSelect != null) {
				if (sqlSelect.count()) {
					return (bindings) -> EntityStatements.buildSelectSet(bindings, sqlSelect).toCountSqlNode();
				}
				return (bindings) -> EntityStatements.buildSelectSet(bindings, sqlSelect).toSqlNode();
			}
		}
		return buildSqlRawFunction(method);
	}

	public static java.util.function.Function<Map<String, Object>, SqlNode> buildSqlRawFunction(Method method) {
		TableAccessible tableAccessible = asTableAccessible(method.getAnnotation(SqlEntity.class));

		{
			SqlRawSimple sqlRaw = method.getAnnotation(SqlRawSimple.class);
			if (sqlRaw != null) {
				if (tableAccessible != null) {
					final TableAccessible finalTableAccessible = tableAccessible;
					return (bindings) -> {
						String sqlText = SqlTextParsers.resolveTableRef(Strings.join(" ", sqlRaw.value()), finalTableAccessible);
						ContainerNode sql = SqlTextParsers.parse(sqlText);
						return buildSqlRaw(sql, varName -> BindingValues.getBindingValueOrDefault(bindings, varName, null));
					};
				}
				return (bindings) -> {
					String sqlText = Strings.join(" ", sqlRaw.value());
					ContainerNode sql = SqlTextParsers.parse(sqlText);
					return buildSqlRaw(sql, varName -> BindingValues.getBindingValueOrDefault(bindings, varName, null));
				};
			}
		}
		{
			SqlRaw sqlRaw = method.getAnnotation(SqlRaw.class);
			if (sqlRaw != null) {
				SqlRawItemModel[] items = SqlRawItemModel.of(sqlRaw);
				final TableAccessible finalTableAccessible = tableAccessible;
				return (bindings) -> {
					// binding-cache
					Map<String, ValueRef<Object>> cache = new HashMap<>();
					return buildSqlRaw(cache, bindings, items, finalTableAccessible);
				};
			}
		}
		// 找不到实体Sql注解，从入参获取直接SQL
		return (bindings) -> {
			Object sql = bindings.get(BindingKeys.SQL);
			if (sql instanceof SqlNode) {
				return (SqlNode) sql;
			}
			if (sql instanceof SqlNodeBuilder) {
				return ((SqlNodeBuilder) sql).toSqlNode();
			}
			throw new IllegalArgumentException("缺少SQL参数：`" + BindingKeys.SQL + "`");
		};
	}


	private static ContainerNode buildSqlRaw(ContainerNode sql, java.util.function.Function<String, Object> valueResolver) {
		sql.visitSubset(node -> {
			if (node.isVarNode() && node.getVarValue() == null) {
				String varName = node.getVarName();
				Object varVal = valueResolver.apply(varName);
				node.bindVarValue(varVal);
			}
		});
		return sql;
	}

	private static ContainerNode buildSqlRaw(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, SqlRawItemModel[] items, TableAccessible tableAccessible) {
		List<SqlRawItemModel> activeItems = new ArrayList<>(items.length);
		SqlRawItemModel defaultItem = null;
		for (SqlRawItemModel item : items) {
			if (isDefaultCondition(item.condition)) {
				defaultItem = item;
			}
			if (evalConditionPredicate(cache, bindings, null, item.condition)) {
				activeItems.add(item);
			}
		}
		if (activeItems.isEmpty() && defaultItem != null) {
			activeItems.add(defaultItem);
		}
		ContainerNode sql = new ContainerNode();

		for (int j = 0, activeItemsSize = activeItems.size(); j < activeItemsSize; j++) {
			if (j > 0) {
				sql.addNode(SqlNodes.BLANK);
			}
			SqlRawItemModel item = activeItems.get(j);
			String forEachKey = item.forEachKey;
			// for each
			if (Strings.isNotBlank(forEachKey)) {
				Object collection = BindingValues.getBindingValueOrDefault(cache, bindings, forEachKey, null);
				if (collection == null) {
					continue;
				}
				boolean isArray = collection.getClass().isArray();
				List<ContainerNode> subSqlList = new ArrayList<>();
				BiConsumer<Integer, Object> itemConsumer = (i, ele) -> {
					if (Strings.isNotBlank(item.itemKey)) cache.put(item.itemKey, ValueRef.of(ele));
					if (Strings.isNotBlank(item.indexKey)) cache.put(item.indexKey, ValueRef.of(i));
					ContainerNode itemSql;
					List<SqlRawItemModel> subset = item.subset;
					if (subset != null) {
						itemSql = buildSqlRaw(cache, bindings, subset.toArray(new SqlRawItemModel[0]), tableAccessible);
					} else {
						itemSql = SqlTextParsers.parse(SqlTextParsers.resolveTableRef(item.sqlText, tableAccessible));
					}
					ContainerNode subSql = buildSqlRaw(itemSql, key -> BindingValues.getBindingValueOrDefault(cache, bindings, key, null));
					subSqlList.add(subSql);
				};

				//array
				if (isArray) {
					int len = Array.getLength(collection);
					for (int i = 0; i < len; i++) {
						final Object ele = Array.get(collection, i);
						itemConsumer.accept(i, ele);
					}
				}
				// iterable
				else if (collection instanceof Iterable) {
					int i = 0;
					for (Object ele : ((Iterable<?>) collection)) {
						itemConsumer.accept(i, ele);
						i++;
					}
				}
				// ignore
				else {
					continue;
				}
				// seperator
				int size = subSqlList.size();
				if (size > 0) {
					sql.addNode(SqlTextParsers.parse(SqlTextParsers.resolveTableRef(item.open, tableAccessible)));
					for (int i = 0; i < size; i++) {
						if (i > 0) {
							sql.addNode(SqlTextParsers.parse(SqlTextParsers.resolveTableRef(item.separator, tableAccessible)));
						}
						sql.addNode(subSqlList.get(i));
					}
					sql.addNode(SqlTextParsers.parse(SqlTextParsers.resolveTableRef(item.close, tableAccessible)));
				}
			}
			// normal
			else {
				ContainerNode itemSql;
				List<SqlRawItemModel> subset = item.subset;
				if (subset != null) {
					itemSql = buildSqlRaw(cache, bindings, subset.toArray(new SqlRawItemModel[0]), tableAccessible);
				} else {
					itemSql = SqlTextParsers.parse(SqlTextParsers.resolveTableRef(item.sqlText, tableAccessible));
				}
				ContainerNode subSql = buildSqlRaw(itemSql, key ->
					BindingValues.getBindingValueOrDefault(cache, bindings, key, null)
				);
				sql.addNode(subSql);
			}
		}
		return sql;
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
			throw new IllegalArgumentException("实体类型不能为空");
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
		SqlSelectSet.Item[] items = sqlSelectSet.value();
		List<SqlSelectSet.Item> activeItems = new ArrayList<>(items.length);
		SqlSelectSet.Item defaultItem = null;
		for (SqlSelectSet.Item item : items) {
			if (isDefaultCondition(item.condition())) {
				defaultItem = item;
			}
			if (!evalConditionPredicate(cache, bindings, null, item.condition())) {
				continue;
			}
			activeItems.add(item);
		}
		if (activeItems.isEmpty() && defaultItem != null) {
			activeItems.add(defaultItem);
		}

		for (SqlSelectSet.Item item : activeItems) {
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

		// 强制添加非逻辑删除条件
		if (sqlSelect.withoutLogicDeleted()) {
			st.getTable().getTableMeta().getColumns().values().stream()
				.filter(c -> c.isLogicDeleted())
				.forEach(meta -> {
					Object val = Converters.convertQuietly(meta.getFieldType(), false);
					st.where().column(meta.getFieldName()).eq(val);
				});
		}

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
			List<SelectColumn> activeCols = new ArrayList<>(columns.length);
			SelectColumn defaultCol = null;
			for (SelectColumn col : columns) {
				if (isDefaultCondition(col.condition())) {
					defaultCol = col;
				}
				if (!evalConditionPredicate(cache, bindings, null, col.condition())) {
					continue;
				}
				activeCols.add(col);
			}
			if (activeCols.isEmpty() && defaultCol != null) {
				activeCols.add(defaultCol);
			}

			for (SelectColumn col : activeCols) {
				String raw = col.raw();
				if (Strings.isNotBlank(raw)) {
					st.selectRaw(raw);
					continue;
				}
				String field = col.field();
				SelectSegment<?, ?> seg = st.select();
				if (Strings.isNotBlank(field)) {
					seg.column(field);
					for (Function function : col.functions()) {
						Tuple3<String, TableField[], Object[]> functionTuple = parseFunction(cache, bindings, function);
						if (functionTuple == null) {
							continue;
						}
						String expr = functionTuple.getFirst();
						TableField[] tableFields = functionTuple.getSecond();
						Object[] args = functionTuple.getThird();
						if (args != null) {
							seg = seg.apply(expr, tableFields, args);
						} else {
							seg = seg.apply(expr, tableFields, bindings);
						}
					}
					seg.aliasWithField(col.aliasWithField());
					seg.alias(col.alias());
					seg.aliasPrefix(col.aliasPrefix());
					seg.aliasSuffix(col.aliasSuffix());
				} else {
					String valueKey = col.valueKey();
					if (Strings.isNotBlank(valueKey)) {
						Object v = BindingValues.getBindingValueOrDefault(cache, bindings, valueKey, null);
						seg.value(v, col.alias());
						seg.aliasPrefix(col.aliasPrefix());
						seg.aliasSuffix(col.aliasSuffix());
					} else {
						throw new IllegalArgumentException("未指定字段名或固定键值");
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
			List<Join> activeJoins = new ArrayList<>();
			Join defaultJoin = null;
			for (Join join : joins) {
				if (isDefaultCondition(join.condition())) {
					defaultJoin = join;
				}
				if (!evalConditionPredicate(cache, bindings, null, join.condition())) {
					continue;
				}
				activeJoins.add(join);
			}
			if (activeJoins.isEmpty() && defaultJoin != null) {
				activeJoins.add(defaultJoin);
			}

			for (Join join : activeJoins) {
				Class<?> joinTable = join.table();
				String joinAlias = join.alias();
				if (joinTable == null || joinTable == void.class) {
					throw new IllegalArgumentException("未指定连接表实体类型");
				}
				if (Strings.isBlank(joinAlias)) {
					throw new IllegalArgumentException("未指定连接表别名");
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
				for (io.polaris.core.jdbc.annotation.segment.Criteria criteria : join.on()) {
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
			List<SelectColumn> activeCols = new ArrayList<>(columns.length);
			SelectColumn defaultCol = null;
			for (SelectColumn col : columns) {
				if (isDefaultCondition(col.condition())) {
					defaultCol = col;
				}
				if (!evalConditionPredicate(cache, bindings, null, col.condition())) {
					continue;
				}
				activeCols.add(col);
			}
			if (activeCols.isEmpty() && defaultCol != null) {
				activeCols.add(defaultCol);
			}

			for (SelectColumn col : activeCols) {
				String raw = col.raw();
				if (Strings.isNotBlank(raw)) {
					st.selectRaw(raw);
					continue;
				}
				String field = col.field();
				SelectSegment<?, ?> seg = st.select();
				if (Strings.isNotBlank(field)) {
					seg.column(field);
					for (Function function : col.functions()) {
						Tuple3<String, TableField[], Object[]> functionTuple = parseFunction(cache, bindings, function);
						if (functionTuple == null) {
							continue;
						}
						String expr = functionTuple.getFirst();
						TableField[] tableFields = functionTuple.getSecond();
						Object[] args = functionTuple.getThird();
						if (args != null) {
							seg = seg.apply(expr, tableFields, args);
						} else {
							seg = seg.apply(expr, tableFields, bindings);
						}
					}
					seg.aliasWithField(col.aliasWithField());
					seg.alias(col.alias());
					seg.aliasPrefix(col.aliasPrefix());
					seg.aliasSuffix(col.aliasSuffix());
				} else {
					String valueKey = col.valueKey();
					if (Strings.isNotBlank(valueKey)) {
						Object v = BindingValues.getBindingValueOrDefault(bindings, valueKey, null);
						seg.value(v, col.alias());
						seg.aliasPrefix(col.aliasPrefix());
						seg.aliasSuffix(col.aliasSuffix());
					} else {
						throw new IllegalArgumentException("未指定字段名或固定键值");
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
		for (io.polaris.core.jdbc.annotation.segment.Criteria criteria : where.criteria()) {
			addWhereByCriteria(cache, bindings, criteria, stWhere);
		}
	}

	private static void addJoinGroupByClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, JoinSegment<?, ?> st
		, GroupBy[] groupBys) {
		if (groupBys != null && groupBys.length > 0) {
			List<GroupBy> activeGroupBys = new ArrayList<>(groupBys.length);
			GroupBy defaultGroupBy = null;
			for (GroupBy groupBy : groupBys) {
				if (isDefaultCondition(groupBy.condition())) {
					defaultGroupBy = groupBy;
				}
				if (!evalConditionPredicate(cache, bindings, null, groupBy.condition())) {
					continue;
				}
				activeGroupBys.add(groupBy);
			}
			if (activeGroupBys.isEmpty() && defaultGroupBy != null) {
				activeGroupBys.add(defaultGroupBy);
			}

			for (GroupBy groupBy : activeGroupBys) {
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
		io.polaris.core.jdbc.annotation.segment.Criteria[] havingCriteria = having.criteria();
		Relation havingRelation = having.relation();
		if (havingCriteria != null && havingCriteria.length > 0) {
			WhereSegment<?, ?> ws = st.having();
			if (havingRelation == Relation.OR) {
				ws = ws.or();
			}
			for (io.polaris.core.jdbc.annotation.segment.Criteria criteria : havingCriteria) {
				addWhereByCriteria(cache, bindings, criteria, ws);
			}
		}
	}

	private static void addJoinOrderByClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, JoinSegment<?, ?> st, Join sqlSelect) {
		io.polaris.core.jdbc.annotation.segment.OrderBy[] orderBys = sqlSelect.orderBy();
		if (orderBys.length > 0) {
			List<io.polaris.core.jdbc.annotation.segment.OrderBy> activeOrderBys = new ArrayList<>(orderBys.length);
			io.polaris.core.jdbc.annotation.segment.OrderBy defaultOrderBy = null;
			for (io.polaris.core.jdbc.annotation.segment.OrderBy orderBy : orderBys) {
				if (isDefaultCondition(orderBy.condition())) {
					defaultOrderBy = orderBy;
				}
				if (!evalConditionPredicate(cache, bindings, null, orderBy.condition())) {
					continue;
				}
				activeOrderBys.add(orderBy);
			}
			if (activeOrderBys.isEmpty() && defaultOrderBy != null) {
				activeOrderBys.add(defaultOrderBy);
			}
			for (io.polaris.core.jdbc.annotation.segment.OrderBy orderBy : activeOrderBys) {
				String raw = orderBy.raw();
				if (Strings.isNotBlank(raw)) {
					st.orderByRaw(raw);
					continue;
				}
				String field = orderBy.field();
				if (Strings.isBlank(field)) {
					throw new IllegalArgumentException("未指定排序字段名");
				}
				OrderBySegment<?, ?> seg = st.orderBy();
				seg.column(field);

				for (Function function : orderBy.functions()) {
					Tuple3<String, TableField[], Object[]> functionTuple = parseFunction(cache, bindings, function);
					if (functionTuple == null) {
						continue;
					}
					String expr = functionTuple.getFirst();
					TableField[] tableFields = functionTuple.getSecond();
					Object[] args = functionTuple.getThird();
					if (args != null) {
						seg = seg.apply(expr, tableFields, args);
					} else {
						seg = seg.apply(expr, tableFields, bindings);
					}
				}
				switch (orderBy.direction()) {
					case ASC:
						seg.asc();
						break;
					case DESC:
						seg.desc();
						break;
				}
			}
		}
	}

	private static void addWhereClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		WhereSegment<?, ?> ws, Where where,
		io.polaris.core.jdbc.annotation.segment.ColumnPredicate columnedPredicate) {
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
		for (io.polaris.core.jdbc.annotation.segment.Criteria criteria : where.criteria()) {
			addWhereByCriteria(cache, bindings, criteria, ws);
		}
	}

	private static void addGroupByClause(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, SelectStatement<?> st
		, GroupBy[] groupBys) {
		if (groupBys != null && groupBys.length > 0) {
			List<GroupBy> activeGroupBys = new ArrayList<>(groupBys.length);
			GroupBy defaultGroupBy = null;
			for (GroupBy groupBy : groupBys) {
				if (isDefaultCondition(groupBy.condition())) {
					defaultGroupBy = groupBy;
				}
				if (!evalConditionPredicate(cache, bindings, null, groupBy.condition())) {
					continue;
				}
				activeGroupBys.add(groupBy);
			}
			if (activeGroupBys.isEmpty() && defaultGroupBy != null) {
				activeGroupBys.add(defaultGroupBy);
			}

			for (GroupBy groupBy : activeGroupBys) {
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
		io.polaris.core.jdbc.annotation.segment.Criteria[] havingCriteria = having.criteria();
		Relation havingRelation = having.relation();
		if (havingCriteria != null && havingCriteria.length > 0) {
			WhereSegment<?, ?> ws = st.having();
			if (havingRelation == Relation.OR) {
				ws = ws.or();
			}
			for (io.polaris.core.jdbc.annotation.segment.Criteria criteria : havingCriteria) {
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
			io.polaris.core.jdbc.annotation.segment.OrderBy[] orderBys = sqlSelect.orderBy();
			if (orderBys.length > 0) {
				List<io.polaris.core.jdbc.annotation.segment.OrderBy> activeOrderBys = new ArrayList<>(orderBys.length);
				io.polaris.core.jdbc.annotation.segment.OrderBy defaultOrderBy = null;
				for (io.polaris.core.jdbc.annotation.segment.OrderBy orderBy : orderBys) {
					if (isDefaultCondition(orderBy.condition())) {
						defaultOrderBy = orderBy;
					}
					if (!evalConditionPredicate(cache, bindings, null, orderBy.condition())) {
						continue;
					}
					activeOrderBys.add(orderBy);
				}
				if (activeOrderBys.isEmpty() && defaultOrderBy != null) {
					activeOrderBys.add(defaultOrderBy);
				}

				for (io.polaris.core.jdbc.annotation.segment.OrderBy orderBy : activeOrderBys) {
					String raw = orderBy.raw();
					if (Strings.isNotBlank(raw)) {
						st.orderByRaw(raw);
						continue;
					}
					String field = orderBy.field();
					if (Strings.isBlank(field)) {
						throw new IllegalArgumentException("未指定排序字段名");
					}
					OrderBySegment<?, ?> seg = st.orderBy();
					seg.column(field);

					for (Function function : orderBy.functions()) {
						Tuple3<String, TableField[], Object[]> functionTuple = parseFunction(cache, bindings, function);
						if (functionTuple == null) {
							continue;
						}
						String expr = functionTuple.getFirst();
						TableField[] tableFields = functionTuple.getSecond();
						Object[] args = functionTuple.getThird();
						if (args != null) {
							seg = seg.apply(expr, tableFields, args);
						} else {
							seg = seg.apply(expr, tableFields, bindings);
						}
					}
					switch (orderBy.direction()) {
						case ASC:
							seg.asc();
							break;
						case DESC:
							seg.desc();
							break;
					}
				}
			}
		}
	}

	private static Tuple3<String, TableField[], Object[]> parseFunction(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, Function function) {
		String expr = function.value();
		if (Strings.isBlank(expr)) {
			return null;
		}
		JoinColumn[] joinColumns = function.joinColumns();
		TableField[] tableFields = new TableField[joinColumns.length];
		for (int i = 0; i < tableFields.length; i++) {
			TableField v = getJoinTableField(cache, bindings, joinColumns[i]);
			if (v == null) {
				// 条件不满足
				return null;
			}
			tableFields[i] = v;
		}
		BindingKey[] bindingKeys = function.bindingKeys();
		if (bindingKeys.length > 0) {
			Object[] args = new Object[bindingKeys.length];
			for (int i = 0; i < bindingKeys.length; i++) {
				Tuple1<?> val = getValForBindingKey(cache, bindings, bindingKeys[i]);
				if (val == null) {
					// 条件不满足
					return null;
				}
				args[i] = val.getFirst();
			}
			return Tuple3.of(expr, tableFields, args);
		} else {
			return Tuple3.of(expr, tableFields, null);
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
		for (Function function : criterion.functions()) {
			Tuple3<String, TableField[], Object[]> functionTuple = parseFunction(cache, bindings, function);
			if (functionTuple == null) {
				continue;
			}
			String expr = functionTuple.getFirst();
			TableField[] tableFields = functionTuple.getSecond();
			Object[] args = functionTuple.getThird();
			if (args != null) {
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
		for (Function function : criterion.functions()) {
			Tuple3<String, TableField[], Object[]> functionTuple = parseFunction(cache, bindings, function);
			if (functionTuple == null) {
				continue;
			}
			String expr = functionTuple.getFirst();
			TableField[] tableFields = functionTuple.getSecond();
			Object[] args = functionTuple.getThird();
			if (args != null) {
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
		for (Function function : criterion.functions()) {
			Tuple3<String, TableField[], Object[]> functionTuple = parseFunction(cache, bindings, function);
			if (functionTuple == null) {
				continue;
			}
			String expr = functionTuple.getFirst();
			TableField[] tableFields = functionTuple.getSecond();
			Object[] args = functionTuple.getThird();
			if (args != null) {
				seg = seg.apply(expr, tableFields, args);
			} else {
				seg = seg.apply(expr, tableFields, bindings);
			}
		}
		return seg;
	}

	private static void addWhereByCriteria(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings,
		io.polaris.core.jdbc.annotation.segment.Criteria criteria, WhereSegment<?, ?> ws) {
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

	private static boolean isDefaultCondition(Condition[] conditions) {
		return conditions.length == 1 && conditions[0].predicateType() == Condition.PredicateType.DEFAULT;
	}

	private static boolean evalConditionPredicate(Map<String, ValueRef<Object>> cache, Map<String, Object> bindings, String defaultBindingKey, Condition[] conditions) {
		// 任意条件不满足，则返回false，否则true
		for (Condition condition : conditions) {
			String key = Strings.coalesce(condition.bindingKey(), defaultBindingKey);
			Object condVal = Strings.isBlank(key) ? null : BindingValues.getBindingValueOrDefault(cache, bindings, key, null);
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
					if (!(condVal instanceof String)) {
						return false;
					}
					String expression = condition.predicateExpression();
					if (Strings.isBlank(expression)) {
						return false;
					}
					if (!Patterns.matches(expression, (String) condVal)) {
						return false;
					}
					break;
				}
				case SCRIPT: {
					String expression = condition.predicateExpression();
					String engineName = condition.predicateScriptEngine();
					if (Strings.isBlank(expression) || Strings.isBlank(engineName)) {
						return false;
					}
					Evaluator evaluator = ScriptEvaluators.getEvaluator(engineName);
					if (evaluator == null) {
						return false;
					}
					Map<String, Object> output = new HashMap<>();
					Object o = evaluator.eval(expression, condVal, output, bindings);
					boolean rs = Converters.convertQuietly(boolean.class, o, false);
					if (!rs) {
						return false;
					}
					break;
				}
				case CUSTOM: {
					String customKey = condition.predicateCustomKey();
					if (Strings.isNotBlank(customKey)) {
						BiPredicate<Map<String, Object>, Object> predicate = (BiPredicate<Map<String, Object>, Object>) BindingValues.getBindingValueOrDefault(cache, bindings, customKey, null);
						if (predicate == null || !predicate.test(bindings, condVal)) {
							return false;
						}
					} else {
						for (Class<? extends BiPredicate<Map<String, Object>, Object>> c : condition.predicateCustomClass()) {
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
				case DEFAULT:
					return false;
				default:
					return false;
			}
		}
		return true;
	}

	public static InsertStatement<?> buildInsert(Map<String, Object> bindings, EntityInsert entityInsert) {
		return buildInsert(bindings, entityInsert.table(), entityInsert.entityKey()
			, entityInsert.enableReplace(), entityInsert.enableUpdateByDuplicateKey()
			, entityInsert.columnPredicate());
	}

	public static InsertStatement<?> buildInsert(Map<String, Object> bindings, Class<?> entityClass, String entityKey,
		boolean enableReplace, boolean enableUpdateByDuplicateKey,
		io.polaris.core.jdbc.annotation.segment.ColumnPredicate predicate
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
		io.polaris.core.jdbc.annotation.segment.ColumnPredicate predicate
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
			st.where().byEntityIdAndVersion(entity);
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
		io.polaris.core.jdbc.annotation.segment.ColumnPredicate predicate,
		io.polaris.core.jdbc.annotation.segment.ColumnPredicate wherePredicate
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
			st.where().byEntityIdAndVersion(entity);
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
			, ConfigurableColumnPredicate.of(bindings, entitySelect.columnPredicate())
			, entitySelect.withoutLogicDeleted());
	}


	public static SelectStatement<?> buildSelect(Map<String, Object> bindings, Class<?> entityClass, String tableAlias, boolean byId, String entityKey, String whereKey, String orderByKey, ColumnPredicate columnPredicate, boolean withoutLogicDeleted) {
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

		if (withoutLogicDeleted) {
			// 强制添加非逻辑删除条件
			st.getTable().getTableMeta().getColumns().values().stream()
				.filter(c -> c.isLogicDeleted())
				.forEach(meta -> {
					Object val = Converters.convertQuietly(meta.getFieldType(), false);
					st.where().column(meta.getFieldName()).eq(val);
				});
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
		io.polaris.core.jdbc.annotation.segment.ColumnPredicate predicate
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


	private static class SqlRawItemModel {

		private static final String CONDITION = Reflects.getPropertyName(SqlRawItem::condition);
		private static final String VALUE = Reflects.getPropertyName(SqlRawItem::value);
		private static final String SUBSET = Reflects.getPropertyName(SqlRawItem::subset);
		private static final String FOR_EACH_KEY = Reflects.getPropertyName(SqlRawItem::forEachKey);
		private static final String ITEM_KEY = Reflects.getPropertyName(SqlRawItem::itemKey);
		private static final String INDEX_KEY = Reflects.getPropertyName(SqlRawItem::indexKey);
		private static final String OPEN = Reflects.getPropertyName(SqlRawItem::open);
		private static final String CLOSE = Reflects.getPropertyName(SqlRawItem::close);
		private static final String SEPARATOR = Reflects.getPropertyName(SqlRawItem::separator);
		private String sqlText;
		private List<SqlRawItemModel> subset;
		private Condition[] condition;
		private String forEachKey;
		private String itemKey;
		private String indexKey;
		private String open;
		private String close;
		private String separator;

		public static SqlRawItemModel[] of(SqlRaw sqlRaw) {
			SqlRawItem[] items = sqlRaw.value();
			SqlRawItemModel[] models = new SqlRawItemModel[items.length];
			for (int i = 0; i < items.length; i++) {
				SqlRawItem item = items[i];
				models[i] = of(item);
			}
			return models;
		}

		public static SqlRawItemModel of(SqlRawItem sqlRawItem) {
			AnnotationAttributes attributes = AnnotationAttributes.of(sqlRawItem);
			return of(attributes);
		}

		private static SqlRawItemModel of(AnnotationAttributes attributes) {
			SqlRawItemModel model = new SqlRawItemModel();
			Object subset = attributes.get(SUBSET);
			int subsetLength = subset == null ? 0 : Array.getLength(subset);
			if (subsetLength > 0) {
				model.subset = new ArrayList<>();
				for (int i = 0; i < subsetLength; i++) {
					Annotation o = (Annotation) Array.get(subset, i);
					model.subset.add(SqlRawItemModel.of(AnnotationAttributes.of(o)));
				}
			} else {
				model.sqlText = Strings.join(" ", attributes.getStringArray(VALUE));
			}
			model.condition = attributes.getAnnotationArray(CONDITION, Condition.class);
			model.forEachKey = attributes.getString(FOR_EACH_KEY);
			model.itemKey = attributes.getString(ITEM_KEY);
			model.indexKey = attributes.getString(INDEX_KEY);
			model.open = Strings.join(" ", attributes.getStringArray(OPEN));
			model.close = Strings.join(" ", attributes.getStringArray(CLOSE));
			model.separator = Strings.join(" ", attributes.getStringArray(SEPARATOR));
			return model;
		}
	}
}

package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.query.Queries;
import io.polaris.core.jdbc.sql.statement.segment.*;
import io.polaris.core.lang.Objs;
import io.polaris.core.reflect.GetterFunction;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
@AnnotationProcessing
public class SelectStatement<S extends SelectStatement<S>> extends BaseStatement<S> implements TableAccessible {

	private boolean distinct = false;
	private final List<SelectSegment<S, ?>> selects = new ArrayList<>();
	private final TableSegment<?> table;
	private final List<JoinSegment<S, ?>> joins = new ArrayList<>();
	private AndSegment<S, ?> where;
	private final List<GroupBySegment<S, ?>> groupBys = new ArrayList<>();
	private AndSegment<S, ?> having;
	private final List<OrderBySegment<S, ?>> orderBys = new ArrayList<>();
	private final Function<String, String> columnDiscovery;
	private final List<Criteria> criteriaList = new ArrayList<>();
	private final List<OrderBy> orderByList = new ArrayList<>();
	private boolean quotaSelectAlias = false;

	@AnnotationProcessing
	public SelectStatement(Class<?> entityClass) {
		this(entityClass, null);
	}

	@AnnotationProcessing
	public SelectStatement(Class<?> entityClass, String alias) {
		this.table = buildTable(entityClass, alias);
		this.columnDiscovery = columnDiscovery();
	}

	public SelectStatement(SelectStatement<?> select, String alias) {
		this.table = buildView(select, alias);
		this.columnDiscovery = columnDiscovery();
	}

	private Function<String, String> columnDiscovery() {
		return field -> {
			String col = null;
			try {
				if (this.table != null) {
					col = this.table.getColumnExpression(field);
				}
			} catch (Exception e) {// 未找到对应的列，忽略此条件字段
			}
			if (col == null) {
				if (!this.joins.isEmpty()) {
					for (JoinSegment<S, ?> join : joins) {
						try {
							col = join.getTable().getColumnExpression(field);
							if (col != null) {
								break;
							}
						} catch (Exception e) {// 未找到对应的列，忽略此条件字段
						}
					}
				}
			}
			return col;
		};
	}

	protected TableSegment<?> buildTable(Class<?> entityClass, String alias) {
		return new TableEntitySegment<>(entityClass, alias);
	}

	protected TableSegment<?> buildView(SelectStatement<?> select, String alias) {
		return new TableViewSegment<>(select, alias);
	}

	@AnnotationProcessing
	protected SelectSegment<S, ?> buildSelect() {
		return new SelectSegment<>(getThis(), this.table);
	}


	protected JoinSegment<S, ?> buildJoin(TextNode conj, Class<?> entityClass, String alias) {
		return new JoinSegment<>(getThis(), conj, entityClass, alias);
	}

	protected JoinSegment<S, ?> buildJoin(TextNode conj, SelectStatement<?> select, String alias) {
		return new JoinSegment<>(getThis(), conj, select, alias);
	}

	@AnnotationProcessing
	protected AndSegment<S, ?> buildWhere() {
		return new AndSegment<>(getThis(), this.table);
	}

	@AnnotationProcessing
	protected GroupBySegment<S, ?> buildGroupBy() {
		return new GroupBySegment<>(getThis(), this.table);
	}

	@AnnotationProcessing
	protected OrderBySegment<S, ?> buildOrderBy() {
		return new OrderBySegment<>(getThis(), this.table);
	}

	public SqlNode toCountSqlNode() {
		ContainerNode sql = new ContainerNode();
		sql.addNode(new TextNode("SELECT COUNT(*) FROM (\n"));
		sqlSelect(sql);
		sqlFrom(sql);
		sqlJoin(sql);
		sqlWhere(sql);
		sqlGroupBy(sql);
		sqlHaving(sql);
		sql.addNode(new TextNode("\n)"));
		return sql;
	}

	@Override
	public SqlNode toSqlNode() {
		// todo 考虑支持集合 union、intersect、minus
		ContainerNode sql = new ContainerNode();
		sqlSelect(sql);
		sqlFrom(sql);
		sqlJoin(sql);
		sqlWhere(sql);
		sqlGroupBy(sql);
		sqlHaving(sql);
		sqlOrderBy(sql);
		return sql;
	}

	@SuppressWarnings("all")
	private void sqlSelect(ContainerNode sql) {
		boolean first = true;
		if (!this.selects.isEmpty()) {
			for (SelectSegment<?, ?> selectSegment : selects) {
				SqlNode sqlNode = selectSegment.toSqlNode(quotaSelectAlias);
				if (sqlNode.isSkipped()) {
					continue;
				}
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				if (first) {
					sql.addNode(SqlNodes.SELECT);
					if (distinct) {
						sql.addNode(SqlNodes.DISTINCT);
					}
					first = false;
				} else {
					sql.addNode(SqlNodes.COMMA);
				}
				sql.addNode(sqlNode);
			}
		}
		if (!this.joins.isEmpty()) {
			for (JoinSegment<S, ?> join : joins) {
				List<? extends SelectSegment<?, ?>> joinSelects = join.getSelects();
				if (!joinSelects.isEmpty()) {
					for (SelectSegment<?, ?> selectSegment : joinSelects) {
						SqlNode sqlNode = selectSegment.toSqlNode(quotaSelectAlias);
						if (sqlNode.isSkipped()) {
							continue;
						}
						if (!sql.isEmpty()) {
							sql.addNode(SqlNodes.LF);
						}
						if (first) {
							sql.addNode(SqlNodes.SELECT);
							if (distinct) {
								sql.addNode(SqlNodes.DISTINCT);
							}
							first = false;
						} else {
							sql.addNode(SqlNodes.COMMA);
						}
						sql.addNode(sqlNode);
					}
				}
			}
		}
	}

	private void sqlFrom(ContainerNode sql) {
		if (this.table != null) {
			if (!sql.isEmpty()) {
				sql.addNode(SqlNodes.LF);
			}
			sql.addNode(SqlNodes.FROM);
			sql.addNode(this.table.toSqlNode());
		}
	}

	private void sqlJoin(ContainerNode sql) {
		if (!this.joins.isEmpty()) {
			for (JoinSegment<S, ?> join : joins) {
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				SqlNode onSqlNode = join.toOnSqlNode();
				boolean skippedOn = onSqlNode.isSkipped();
				if (skippedOn) {
					sql.addNode(SqlNodes.COMMA);
				} else {
					sql.addNode(join.getConj());
				}
				sql.addNode(join.getTable().toSqlNode());
				if (!skippedOn) {
					sql.addNode(SqlNodes.ON);
					sql.addNode(onSqlNode);
				}
			}
		}
	}

	private void sqlWhere(ContainerNode sql) {
		boolean first = true;
		if (this.where != null) {
			SqlNode sqlNode = this.where.toSqlNode();
			if (!sqlNode.isSkipped()) {
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				sql.addNode(SqlNodes.WHERE);
				first = false;
				sql.addNode(sqlNode);
			}
		}
		if (!this.joins.isEmpty()) {
			for (JoinSegment<S, ?> join : joins) {
				SqlNode sqlNode = join.toWhereSqlNode();
				if (sqlNode.isSkipped()) {
					continue;
				}
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				if (first) {
					sql.addNode(SqlNodes.WHERE);
					first = false;
				} else {
					sql.addNode(SqlNodes.AND);
				}
				sql.addNode(sqlNode);
			}
		}
		if (!this.criteriaList.isEmpty()) {
			for (Criteria criteria : criteriaList) {
				SqlNode sqlNode = Queries.parse(criteria, false, columnDiscovery);
				if (sqlNode.isSkipped()) {
					continue;
				}
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				if (first) {
					sql.addNode(SqlNodes.WHERE);
					first = false;
				} else {
					sql.addNode(SqlNodes.AND);
				}
				sql.addNode(SqlNodes.LEFT_PARENTHESIS);
				sql.addNode(sqlNode);
				sql.addNode(SqlNodes.RIGHT_PARENTHESIS);
			}
		}
	}

	private void sqlGroupBy(ContainerNode sql) {
		if (!groupBys.isEmpty()) {
			boolean first = true;
			for (GroupBySegment<S, ?> groupBy : groupBys) {
				SqlNode sqlNode = groupBy.toSqlNode();
				if (sqlNode.isSkipped()) {
					continue;
				}
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				if (first) {
					sql.addNode(SqlNodes.GROUP_BY);
					first = false;
				} else {
					sql.addNode(SqlNodes.COMMA);
				}
				sql.addNode(sqlNode);
			}

		}
	}

	private void sqlHaving(ContainerNode sql) {
		if (having != null) {
			SqlNode sqlNode = having.toSqlNode();
			if (!sqlNode.isSkipped()) {
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				sql.addNode(SqlNodes.HAVING);
				sql.addNode(sqlNode);
			}
		}
	}

	private void sqlOrderBy(ContainerNode sql) {
		boolean first = true;
		if (!this.orderBys.isEmpty()) {
			for (OrderBySegment<S, ?> orderBy : this.orderBys) {
				SqlNode sqlNode = orderBy.toSqlNode();
				if (sqlNode.isSkipped()) {
					continue;
				}
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				if (first) {
					sql.addNode(SqlNodes.ORDER_BY);
					first = false;
				} else {
					sql.addNode(SqlNodes.COMMA);
				}
				sql.addNode(sqlNode);
			}
		}
		if (!this.orderByList.isEmpty()) {
			for (OrderBy orderBy : this.orderByList) {
				SqlNode sqlNode = Queries.parse(orderBy, columnDiscovery);
				if (sqlNode.isSkipped()) {
					continue;
				}
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				if (first) {
					sql.addNode(SqlNodes.ORDER_BY);
					first = false;
				} else {
					sql.addNode(SqlNodes.COMMA);
				}
				sql.addNode(sqlNode);
			}
		}
	}


	public <J extends JoinSegment<S, J>> JoinSegmentDriver<S, J> join(JoinBuilder<S, J> builder) {
		return new JoinSegmentDriver<>(getThis(), joins::add, builder);
	}


	public JoinSegment<S, ?> join(SelectStatement<?> select, String alias) {
		JoinSegment<S, ?> join = buildJoin(SqlNodes.JOIN, select, alias);
		joins.add(join);
		return join;
	}

	public JoinSegment<S, ?> innerJoin(SelectStatement<?> select, String alias) {
		JoinSegment<S, ?> join = buildJoin(SqlNodes.INNER_JOIN, select, alias);
		joins.add(join);
		return join;
	}


	public JoinSegment<S, ?> leftJoin(SelectStatement<?> select, String alias) {
		JoinSegment<S, ?> join = buildJoin(SqlNodes.LEFT_JOIN, select, alias);
		joins.add(join);
		return join;
	}

	public JoinSegment<S, ?> rightJoin(SelectStatement<?> select, String alias) {
		JoinSegment<S, ?> join = buildJoin(SqlNodes.RIGHT_JOIN, select, alias);
		joins.add(join);
		return join;
	}

	public JoinSegment<S, ?> outerJoin(SelectStatement<?> select, String alias) {
		JoinSegment<S, ?> join = buildJoin(SqlNodes.OUTER_JOIN, select, alias);
		joins.add(join);
		return join;
	}


	public JoinSegment<S, ?> join(Class<?> entityClass, String alias) {
		JoinSegment<S, ?> join = buildJoin(SqlNodes.JOIN, entityClass, alias);
		joins.add(join);
		return join;
	}

	public JoinSegment<S, ?> innerJoin(Class<?> entityClass, String alias) {
		JoinSegment<S, ?> join = buildJoin(SqlNodes.INNER_JOIN, entityClass, alias);
		joins.add(join);
		return join;
	}

	public JoinSegment<S, ?> leftJoin(Class<?> entityClass, String alias) {
		JoinSegment<S, ?> join = buildJoin(SqlNodes.LEFT_JOIN, entityClass, alias);
		joins.add(join);
		return join;
	}

	public JoinSegment<S, ?> rightJoin(Class<?> entityClass, String alias) {
		JoinSegment<S, ?> join = buildJoin(SqlNodes.RIGHT_JOIN, entityClass, alias);
		joins.add(join);
		return join;
	}

	public JoinSegment<S, ?> outerJoin(Class<?> entityClass, String alias) {
		JoinSegment<S, ?> join = buildJoin(SqlNodes.OUTER_JOIN, entityClass, alias);
		joins.add(join);
		return join;
	}

	public JoinSegment<S, ?> join(Class<?> entityClass) {
		JoinSegment<S, ?> join = join(entityClass, null);
		joins.add(join);
		return join;
	}

	public JoinSegment<S, ?> innerJoin(Class<?> entityClass) {
		return innerJoin(entityClass, null);
	}

	public JoinSegment<S, ?> leftJoin(Class<?> entityClass) {
		return leftJoin(entityClass, null);
	}

	public JoinSegment<S, ?> rightJoin(Class<?> entityClass) {
		return rightJoin(entityClass, null);
	}

	public JoinSegment<S, ?> outerJoin(Class<?> entityClass) {
		return outerJoin(entityClass, null);
	}

	public S distinct() {
		this.distinct = true;
		return getThis();
	}

	@AnnotationProcessing
	@SuppressWarnings("unchecked")
	public <T extends SelectSegment<S, T>> T select() {
		SelectSegment<S, ?> segment = buildSelect();
		this.selects.add(segment);
		return (T) segment;
	}

	public S quotaSelectAlias(boolean quotaSelectAlias) {
		this.quotaSelectAlias = quotaSelectAlias;
		return getThis();
	}

	public S selectAll() {
		if (this.table == null) {
			throw new IllegalStateException("no table");
		}
		SelectSegment<S, ?> segment = buildSelect().column(SymbolConsts.ASTERISK);
		selects.add(segment);
		return getThis();
	}

	public S selectRaw(String... rawColumns) {
		return select(new TextNode(Strings.join(",", rawColumns)));
	}

	public S select(SqlNode sqlNode) {
		SelectSegment<S, ?> segment = buildSelect().sql(sqlNode);
		selects.add(segment);
		return getThis();
	}

	public <T, R> S select(GetterFunction<T, R> getter) {
		return select(Reflects.getPropertyName(getter));
	}

	public S select(String field) {
		if (this.table == null) {
			throw new IllegalStateException("no table");
		}
		SelectSegment<S, ?> segment = buildSelect().column(field);
		selects.add(segment);
		return getThis();
	}

	public <T, R> S select(GetterFunction<T, R> getter, String alias) {
		return select(Reflects.getPropertyName(getter), alias);
	}

	public S select(String field, String alias) {
		if (this.table == null) {
			throw new IllegalStateException("no table");
		}
		SelectSegment<S, ?> segment = buildSelect().column(field);
		segment.alias(alias);
		selects.add(segment);
		return getThis();
	}

	@AnnotationProcessing
	@SuppressWarnings("unchecked")
	public <W extends AndSegment<S, W>> W where() {
		return (W) (where = Objs.defaultIfNull(where, this::buildWhere));
	}


	public S where(Criteria criteria) {
		if (criteria != null) {
			criteriaList.add(criteria);
		}
		return getThis();
	}


	@AnnotationProcessing
	@SuppressWarnings("unchecked")
	public <G extends GroupBySegment<S, G>> G groupBy() {
		GroupBySegment<S, ?> groupBy = buildGroupBy();
		groupBys.add(groupBy);
		return (G) groupBy;
	}

	@AnnotationProcessing
	@SuppressWarnings("unchecked")
	public <H extends AndSegment<S, H>> H having() {
		return (H) (having = Objs.defaultIfNull(having, this::buildWhere));
	}

	@AnnotationProcessing
	@SuppressWarnings("unchecked")
	public <E extends OrderBySegment<S, E>> E orderBy() {
		OrderBySegment<S, ?> orderBy = buildOrderBy();
		orderBys.add(orderBy);
		return (E) orderBy;
	}

	public S orderBy(OrderBy orderBy) {
		if (orderBy != null) {
			orderByList.add(orderBy);
		}
		return getThis();
	}

	public S orderByRaw(String... rawSql) {
		for (String s : rawSql) {
			OrderBySegment<S, ?> orderBy = buildOrderBy();
			orderBy.sql(new TextNode(s));
			orderBys.add(orderBy);
		}
		return getThis();
	}


	public <T, R> S orderBy(GetterFunction<T, R> getter) {
		return orderBy(Reflects.getPropertyName(getter));
	}

	public S orderBy(String field) {
		OrderBySegment<S, ?> orderBy = buildOrderBy();
		orderBy.column(field);
		orderBys.add(orderBy);
		return getThis();
	}

	public <T, R> S orderByDesc(GetterFunction<T, R> getter) {
		return orderByDesc(Reflects.getPropertyName(getter));
	}

	public S orderByDesc(String field) {
		OrderBySegment<S, ?> orderBy = buildOrderBy();
		orderBy.column(field).desc();
		orderBys.add(orderBy);
		return getThis();
	}


	public TableAccessible getTableAccessible() {
		return this;
	}

	@AnnotationProcessing
	public TableSegment<?> getTable() {
		return this.table;
	}

	@Override
	public TableSegment<?> getTable(String tableAlias) {
		if (Objs.equals(this.table.getTableAlias(), tableAlias)) {
			return this.table;
		}
		for (JoinSegment<S, ?> join : joins) {
			TableSegment<?> joinTable = join.getTable();
			if (Objs.equals(joinTable.getTableAlias(), tableAlias)) {
				return joinTable;
			}
		}
		throw new IllegalStateException("找不到对应的表");
	}


	@Override
	public TableSegment<?> getTable(int tableIndex) {
		if (tableIndex == 0) {
			return this.table;
		}
		int size = 1 + joins.size();
		while (tableIndex < 0) {
			tableIndex += size;
		}
		if (tableIndex > 0 && tableIndex < size) {
			return joins.get(tableIndex - 1).getTable();
		}
		throw new IllegalStateException("找不到对应的表");
	}

	public List<String> getSelectRawColumns() {
		List<String> list = new ArrayList<>();
		if (!this.selects.isEmpty()) {
			for (SelectSegment<S, ?> select : this.selects) {
				list.addAll(select.getSelectRawColumns());
			}
		}
		if (!this.joins.isEmpty()) {
			for (JoinSegment<S, ?> join : this.joins) {
				for (SelectSegment<?, ?> select : join.getSelects()) {
					list.addAll(select.getSelectRawColumns());
				}
			}
		}
		return list;
	}

	public boolean hasSelectRawColumn(String columnOrAlias) {
		if (!this.selects.isEmpty()) {
			for (SelectSegment<S, ?> select : this.selects) {
				if (select.hasSelectRawColumn(columnOrAlias)) {
					return true;
				}
			}
		}
		if (!this.joins.isEmpty()) {
			for (JoinSegment<S, ?> join : this.joins) {
				for (SelectSegment<?, ?> select : join.getSelects()) {
					if (select.hasSelectRawColumn(columnOrAlias)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}

package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.Queries;
import io.polaris.core.jdbc.sql.statement.segment.*;
import io.polaris.core.lang.Objs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
@AnnotationProcessing
public class DeleteStatement<S extends DeleteStatement<S>> extends BaseStatement<S> implements TableAccessible{

	private final TableSegment<?> table;
	private AndSegment<S, ?> where;
	private final Function<String, String> columnDiscovery;
	private final List<Criteria> criteriaList = new ArrayList<>();

	@AnnotationProcessing
	public DeleteStatement(Class<?> entityClass) {
		this(entityClass, null);
	}

	@AnnotationProcessing
	public DeleteStatement(Class<?> entityClass, String alias) {
		this.table = TableSegment.fromEntity(entityClass, alias);
		this.columnDiscovery = columnDiscovery();
	}

	public static DeleteStatement<?> of(Class<?> entityClass, String alias){
		return new DeleteStatement<>(entityClass, alias);
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
			return col;
		};
	}

	@AnnotationProcessing
	protected AndSegment<S, ?> buildWhere() {
		return new AndSegment<>(getThis(), this.table);
	}


	@Override
	public SqlNode toSqlNode() {
		ContainerNode sql = new ContainerNode();
		sqlDelete(sql);
		sqlWhere(sql);
		return sql;
	}

	private void sqlDelete(ContainerNode sql) {
		if (this.table != null) {
			if (!sql.isEmpty()) {
				sql.addNode(SqlNodes.LF);
			}
			sql.addNode(SqlNodes.DELETE);
			sql.addNode(SqlNodes.FROM);
			sql.addNode(this.table.toSqlNode());
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
				first = false;
				sql.addNode(SqlNodes.WHERE);
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

	public S where(Criteria criteria) {
		if (criteria != null) {
			criteriaList.add(criteria);
		}
		return getThis();
	}


	@AnnotationProcessing
	@SuppressWarnings("unchecked")
	public <W extends AndSegment<S, W>> W where() {
		return (W) (where = Objs.defaultIfNull(where, this::buildWhere));
	}


	@AnnotationProcessing
	public TableSegment<?> getTable() {
		return table;
	}

	@Override
	public TableSegment<?> getTable(int tableIndex) {
		// 不支持负数定位
		if (tableIndex < 0) {
			throw new IllegalArgumentException("tableIndex: " + tableIndex);
		}
		if (tableIndex == 0) {
			return this.table;
		}
		throw new IllegalArgumentException("no such table! tableIndex: " + tableIndex);
	}

	@Override
	public TableSegment<?> getTable(String tableAlias) {
		if (Objs.equals(this.table.getTableAlias(), tableAlias)) {
			return this.table;
		}
		throw new IllegalArgumentException("no such table! tableAlias: " + tableAlias);
	}
}

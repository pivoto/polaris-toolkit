package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.tuple.Tuple2;
import io.polaris.core.tuple.Tuples;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since  Dec 30, 2023
 */
public class SetOpsStatement<S extends SetOpsStatement<S>> extends BaseStatement<S> {

	private SelectStatement<?> firstSelect;
	private SqlNodeBuilder first;
	private List<Tuple2<SqlNode, SqlNodeBuilder>> others = new ArrayList<>();

	public SetOpsStatement(SelectStatement<?> firstSql) {
		this.firstSelect = firstSql;
		this.first = firstSql;
	}

	public static SetOpsStatement<?> of(SelectStatement<?> firstSql) {
		return new SetOpsStatement<>(firstSql);
	}

	public SqlNode toCountSqlNode() {
		ContainerNode sql = new ContainerNode();
		sql.addNode(new TextNode("SELECT COUNT(*) FROM ("));
		sql.addNode(SqlNodes.LF);
		sql.addNode(toSqlNode());
		sql.addNode(SqlNodes.LF);
		sql.addNode(new TextNode(") _tbl"));
		return sql;
	}

	@Override
	public SqlNode toSqlNode() {
		ContainerNode sql = new ContainerNode();
		sql.addNode(first.toSqlNode());
		for (Tuple2<SqlNode, SqlNodeBuilder> tuple : others) {
			sql.addNode(SqlNodes.LF);
			sql.addNode(tuple.getFirst());
			sql.addNode(SqlNodes.LF);
			sql.addNode(tuple.getSecond().toSqlNode());
		}
		return sql;
	}

	public S union(SelectStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.UNION, sql));
		return getThis();
	}

	public S union(SetOpsStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.UNION, sql));
		return getThis();
	}

	public S unionAll(SelectStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.UNION_ALL, sql));
		return getThis();
	}

	public S unionAll(SetOpsStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.UNION_ALL, sql));
		return getThis();
	}

	public S intersect(SelectStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.INTERSECT, sql));
		return getThis();
	}

	public S intersect(SetOpsStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.INTERSECT, sql));
		return getThis();
	}

	public S intersectAll(SelectStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.INTERSECT_ALL, sql));
		return getThis();
	}

	public S intersectAll(SetOpsStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.INTERSECT_ALL, sql));
		return getThis();
	}

	public S minus(SelectStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.MINUS, sql));
		return getThis();
	}

	public S minus(SetOpsStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.MINUS, sql));
		return getThis();
	}

	public S minusAll(SelectStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.MINUS_ALL, sql));
		return getThis();
	}

	public S minusAll(SetOpsStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.MINUS_ALL, sql));
		return getThis();
	}

	public S except(SelectStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.EXCEPT, sql));
		return getThis();
	}

	public S except(SetOpsStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.EXCEPT, sql));
		return getThis();
	}

	public S exceptAll(SelectStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.EXCEPT_ALL, sql));
		return getThis();
	}

	public S exceptAll(SetOpsStatement<?> sql) {
		this.others.add(Tuples.of(SqlNodes.EXCEPT_ALL, sql));
		return getThis();
	}

	public List<String> getSelectRawColumns() {
		return this.firstSelect.getSelectRawColumns();
	}

	public boolean hasSelectRawColumn(String columnOrAlias) {
		return this.firstSelect.hasSelectRawColumn(columnOrAlias);
	}
}

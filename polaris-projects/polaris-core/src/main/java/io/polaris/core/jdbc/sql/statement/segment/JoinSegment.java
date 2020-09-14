package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.lang.Objs;
import io.polaris.core.reflect.GetterFunction;
import io.polaris.core.reflect.Reflects;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
@AnnotationProcessing
public class JoinSegment<O extends Segment<O>, S extends JoinSegment<O, S>> extends BaseSegment<S> implements TableAccessible {
	private final O owner;
	private final TableAccessible tableAccessible;
	private final TextNode conj;
	private final TableSegment<?> table;
	private final List<SelectSegment<S, ?>> selects = new ArrayList<>();
	private AndSegment<S, ?> on;
	private AndSegment<S, ?> where;


	@AnnotationProcessing
	public JoinSegment(O owner, TextNode conj, Class<?> entityClass, String alias) {
		this.owner = owner;
		this.conj = conj;
		this.table = buildTable(entityClass, alias);
		this.tableAccessible = fetchTableAccessible();
	}

	@AnnotationProcessing
	public JoinSegment(O owner, TextNode conj, SelectStatement<?> select, String alias) {
		this.owner = owner;
		this.conj = conj;
		this.table = buildView(select, alias);
		this.tableAccessible = fetchTableAccessible();
	}

	private TableAccessible fetchTableAccessible() {
		if (owner instanceof TableAccessible) {
			return (TableAccessible) owner;
		}
		if (owner instanceof TableAccessibleHolder) {
			return ((TableAccessibleHolder) owner).getTableAccessible();
		}
		throw new IllegalArgumentException("owner error!");
	}

	@Override
	public TableAccessible getTableAccessible() {
		return tableAccessible;
	}

	@SuppressWarnings({"unchecked"})
	protected <T extends TableSegment<T>> T buildTable(Class<?> entityClass, String alias) {
		return (T) new TableEntitySegment<>(entityClass, alias);
	}

	@SuppressWarnings("unchecked")
	protected <T extends TableSegment<T>> T buildView(SelectStatement<?> select, String alias) {
		return (T) new TableViewSegment<>(select, alias);
	}

	@AnnotationProcessing
	@SuppressWarnings("unchecked")
	protected <T extends SelectSegment<S, T>> T buildSelect() {
		return (T) new SelectSegment<>(getThis(), this.table);
	}


	@AnnotationProcessing
	@SuppressWarnings("unchecked")
	protected <T extends AndSegment<S, T>> T buildWhere() {
		return (T) new AndSegment<>(getThis(), this.table);
	}

	public SqlNode toOnSqlNode() {
		if (on == null) {
			return SqlNodes.EMPTY;
		}
		return on.toSqlNode();
	}

	public SqlNode toWhereSqlNode() {
		if (where == null) {
			return SqlNodes.EMPTY;
		}
		return where.toSqlNode();
	}

	public O end() {
		return this.owner;
	}

	@AnnotationProcessing
	@SuppressWarnings("unchecked")
	public <T extends AndSegment<S, T>> T on() {
		return (T) (on = Objs.defaultIfNull(on, this::buildWhere));
	}

	@AnnotationProcessing
	@SuppressWarnings("unchecked")
	public <T extends AndSegment<S, T>> T where() {
		return (T) (where = Objs.defaultIfNull(where, this::buildWhere));
	}


	public S selectAll() {
		SelectSegment<S, ?> segment = buildSelect().column(SymbolConsts.ASTERISK);
		selects.add(segment);
		return getThis();
	}

	public S selectAll(boolean withFieldAlias) {
		SelectSegment<S, ?> segment = buildSelect().column(SymbolConsts.ASTERISK).aliasWithField(withFieldAlias);
		selects.add(segment);
		return getThis();
	}

	public S select(SqlNode sqlNode) {
		SelectSegment<S, ?> segment = buildSelect().sql(sqlNode);
		selects.add(segment);
		return getThis();
	}

	@SuppressWarnings("unchecked")
	@AnnotationProcessing
	public <T extends SelectSegment<S, T>> T select() {
		SelectSegment<S, ?> segment = buildSelect();
		this.selects.add(segment);
		return (T) segment;
	}

	public <T, R> S select(GetterFunction<T, R> getter) {
		return select(Reflects.getPropertyName(getter));
	}

	@AnnotationProcessing
	public S select(String field) {
		SelectSegment<S, ?> segment = buildSelect().column(field);
		selects.add(segment);
		return getThis();
	}

	public <T, R> S select(GetterFunction<T, R> getter, String alias) {
		return select(Reflects.getPropertyName(getter), alias);
	}

	@AnnotationProcessing
	public S select(String field, String alias) {
		SelectSegment<S, ?> segment = buildSelect().column(field);
		segment.alias(alias);
		selects.add(segment);
		return getThis();
	}

	public List<SelectSegment<S, ?>> getSelects() {
		return selects;
	}

	public TextNode getConj() {
		return conj;
	}

	public TableSegment<?> getTable() {
		return table;
	}


	@Override
	public TableSegment<?> getTable(int tableIndex) {
		return getTableAccessible().getTable(tableIndex);
	}

	@Override
	public TableSegment<?> getTable(String tableAlias) {
		return getTableAccessible().getTable(tableAlias);
	}

}

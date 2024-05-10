package io.polaris.core.jdbc.sql.statement.segment;

import java.util.Map;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.consts.StdConsts;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.SqlTextParsers;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.jdbc.sql.statement.expression.AggregateFunction;
import io.polaris.core.jdbc.sql.statement.expression.Expression;
import io.polaris.core.jdbc.sql.statement.expression.Expressions;
import io.polaris.core.reflect.GetterFunction;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since  Aug 20, 2023
 */
@AnnotationProcessing
public class OrderBySegment<O extends Segment<O>, S extends OrderBySegment<O, S>> extends BaseSegment<S> implements SqlNodeBuilder {
	private final O owner;
	private final TableSegment<?> table;
	private final TableAccessible tableAccessible;
	private boolean asc = true;
	private String field;
	private transient String _rawColumn;
	private SqlNode sql;
	/** 表达式 */
	private ExpressionSegment<?> expression;

	@AnnotationProcessing
	public OrderBySegment(O owner, TableSegment<?> table) {
		this.owner = owner;
		this.table = table;
		this.tableAccessible = fetchTableAccessible();
	}

	private TableAccessible fetchTableAccessible() {
		if (owner instanceof TableAccessible) {
			return (TableAccessible) owner;
		}
		if (owner instanceof TableAccessibleHolder) {
			return ((TableAccessibleHolder) owner).getTableAccessible();
		}
		return null;
	}

	@Override
	public SqlNode toSqlNode() {
		if (sql != null) {
			return sql;
		}
		String column = column();
		if (Strings.isBlank(column)) {
			return SqlNodes.EMPTY;
		}
		if (expression != null) {
			return expression.toSqlNode(column);
		}

		ContainerNode sql = new ContainerNode();
		sql.addNode(new TextNode(column));
		if (asc) {
			sql.addNode(SqlNodes.ASC);
		} else {
			sql.addNode(SqlNodes.DESC);
		}
		return sql;
	}

	private String column() {
		if (Strings.isNotBlank(_rawColumn)) {
			return _rawColumn;
		}
		if (table == null || Strings.isBlank(field)) {
			return SymbolConsts.EMPTY;
		}
		this._rawColumn = table.getColumnExpression(field);
		return _rawColumn;
	}

	public O end() {
		return owner;
	}


	public <T, R> S column(GetterFunction<T, R> getter) {
		return column(Reflects.getPropertyName(getter));
	}

	@AnnotationProcessing
	public S column(String field) {
		this.field = field;
		return getThis();
	}

	public S sql(SqlNode sql) {
		this.sql = sql;
		return getThis();
	}

	public S rawColumn(String rawColumn) {
		rawColumn = SqlTextParsers.resolveTableRef(rawColumn, tableAccessible);
		this._rawColumn = rawColumn;
		return getThis();
	}

	public O asc() {
		asc = true;
		return end();
	}

	public O desc() {
		asc = false;
		return end();
	}

	public S apply(String functionPattern, TableField[] extFields, Map<String, Object> bindings) {
		return apply(Expressions.pattern(functionPattern), extFields, bindings);
	}

	public S apply(String functionPattern, TableField[] extFields, Object... bindings) {
		return apply(Expressions.pattern(functionPattern), extFields, bindings);
	}

	public S apply(String functionPattern, TableField... extFields) {
		return apply(Expressions.pattern(functionPattern), extFields);
	}

	public S apply(String functionPattern) {
		return apply(Expressions.pattern(functionPattern), StdConsts.EMPTY_ARRAY);
	}

	public S apply(String functionPattern, Map<String, Object> bindings) {
		return apply(Expressions.pattern(functionPattern), bindings);
	}

	public S apply(String functionPattern, Object[] bindings) {
		return apply(Expressions.pattern(functionPattern), bindings);
	}

	public S apply(Expression function) {
		this.expression = new ExpressionSegment<>(this.expression, function, StdConsts.EMPTY_ARRAY);
		return getThis();
	}

	public S apply(Expression function, Map<String, Object> bindings) {
		this.expression = new ExpressionSegment<>(this.expression, function, bindings);
		return getThis();
	}

	public S apply(Expression function, Object[] bindings) {
		this.expression = new ExpressionSegment<>(this.expression, function, bindings);
		return getThis();
	}

	public S apply(Expression function, TableField... extFields) {
		return apply(function, extFields, StdConsts.EMPTY_ARRAY);
	}

	public S apply(Expression function, TableField[] extFields, Map<String, Object> bindings) {
		this.expression = new ExpressionSegment<>(this.expression, tableAccessible, extFields, function, bindings);
		return getThis();
	}

	public S apply(Expression function, TableField[] extFields, Object... bindings) {
		this.expression = new ExpressionSegment<>(this.expression, tableAccessible, extFields, function, bindings);
		return getThis();
	}

	public S apply(String functionPattern, int tableIndex, String field) {
		return apply(functionPattern, TableField.of(tableIndex, field));
	}

	public S apply(Expression function, int tableIndex, String field) {
		return apply(function, TableField.of(tableIndex, field));
	}

	public S apply(String functionPattern, String tableAlias, String field) {
		return apply(functionPattern, TableField.of(tableAlias, field));
	}

	public S apply(Expression function, String tableAlias, String field) {
		return apply(function, TableField.of(tableAlias, field));
	}

	public S count() {
		this.expression = new ExpressionSegment<>(this.expression, AggregateFunction.COUNT.getExpression());
		return getThis();
	}

	public S sum() {
		this.expression = new ExpressionSegment<>(this.expression, AggregateFunction.SUM.getExpression());
		return getThis();
	}

	public S max() {
		this.expression = new ExpressionSegment<>(this.expression, AggregateFunction.MAX.getExpression());
		return getThis();
	}

	public S min() {
		this.expression = new ExpressionSegment<>(this.expression, AggregateFunction.MIN.getExpression());
		return getThis();
	}

	public S avg() {
		this.expression = new ExpressionSegment<>(this.expression, AggregateFunction.AVG.getExpression());
		return getThis();
	}



	public TableSegment<?> getTable() {
		return table;
	}
}

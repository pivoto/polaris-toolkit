package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.StdConsts;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.jdbc.sql.statement.expression.AggregateFunction;
import io.polaris.core.jdbc.sql.statement.expression.Expression;
import io.polaris.core.jdbc.sql.statement.expression.PatternExpression;
import io.polaris.core.reflect.GetterFunction;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;

import java.util.Collections;
import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
@AnnotationProcessing
public class SelectSegment<O extends Segment<O>, S extends SelectSegment<O, S>> extends BaseSegment<S> implements SqlNodeBuilder {
	private final O owner;
	private final TableSegment<? extends TableSegment<?>> table;
	private final TableAccessible tableAccessible;
	private String field;
	private String alias;
	private SqlNode sql;
	private boolean aliasWithField = true;
	/** 固定列值 */
	private Object value;
	/** 表达式 */
	private ExpressionSegment<?> expression;

	@AnnotationProcessing
	public SelectSegment(O owner, TableSegment<? extends TableSegment<?>> table) {
		this.owner = owner;
		this.table = table;
		this.tableAccessible = getTableAccessible();
	}

	private TableAccessible getTableAccessible() {
		if (owner instanceof SelectSegment) {
			return ((SelectSegment<?, ?>) owner).getTableAccessible();
		}
		if (owner instanceof SelectStatement) {
			return ((SelectStatement<?>) owner).getTableAccessible();
		}
		if (owner instanceof JoinSegment) {
			return ((JoinSegment<?, ?>) owner).getTableAccessible();
		}
		// 暂不支持复杂Update语句
		return null;
	}

	@Override
	public SqlNode toSqlNode() {
		return toSqlNode(false);
	}

	public SqlNode toSqlNode(boolean quotaAlias) {
		if (sql != null) {
			return sql;
		}
		if (value != null && Strings.isNotBlank(alias)) {
			String fieldAlias = this.alias;
			ContainerNode containerNode = new ContainerNode();
			containerNode.addNode(SqlNodes.dynamic(fieldAlias, value));
			if (quotaAlias && !fieldAlias.startsWith("\"")) {
				containerNode.addNode(new TextNode(" \"" + fieldAlias + "\""));
			} else {
				containerNode.addNode(new TextNode(" " + fieldAlias));
			}
			return containerNode;
		}
		if (table != null && Strings.isNotBlank(field)) {
			if (field.equals(SymbolConsts.ASTERISK)) {
				String fieldAlias = this.alias;
				if (expression != null) {
					if (Strings.isNotBlank(fieldAlias)) {
						ContainerNode containerNode = new ContainerNode();
						containerNode.addNode(expression.toSqlNode(field));
						if (quotaAlias && !fieldAlias.startsWith("\"")) {
							containerNode.addNode(new TextNode(" \"" + fieldAlias + "\""));
						} else {
							containerNode.addNode(new TextNode(" " + fieldAlias));
						}
						return containerNode;
					} else {
						return expression.toSqlNode(field);
					}
				}
				return new TextNode(table.getAllColumnExpression(aliasWithField, quotaAlias));
			} else {
				String fieldAlias = this.alias;
				if (Strings.isBlank(fieldAlias) && aliasWithField) {
					fieldAlias = field;
				}
				String columnExpression = table.getColumnExpression(field);
				if (expression != null) {
					if (Strings.isNotBlank(fieldAlias)) {
						ContainerNode containerNode = new ContainerNode();
						containerNode.addNode(expression.toSqlNode(columnExpression));
						containerNode.addNode(new TextNode(" " + fieldAlias));
						return containerNode;
					} else {
						return expression.toSqlNode(columnExpression);
					}
				}
				if (Strings.isNotBlank(fieldAlias)) {
					if (quotaAlias && !fieldAlias.startsWith("\"")) {
						return new TextNode(columnExpression + "  \"" + fieldAlias + "\"");
					} else {
						return new TextNode(columnExpression + " " + fieldAlias);
					}
				} else {
					return new TextNode(columnExpression);
				}
			}
		}
		return SqlNodes.EMPTY;
	}

	public List<String> getSelectRawColumns() {
		if (sql != null) {
			return Collections.emptyList();
		}
		if (value != null && Strings.isNotBlank(alias)) {
			return Iterables.asList(alias);
		}
		if (table != null && Strings.isNotBlank(field)) {
			if (expression != null) {
				String fieldAlias = this.alias;
				if (Strings.isBlank(fieldAlias) && aliasWithField && !field.equals(SymbolConsts.ASTERISK)) {
					fieldAlias = field;
				}
				return Iterables.asList(fieldAlias);
			} else {
				if (field.equals(SymbolConsts.ASTERISK)) {
					if (aliasWithField) {
						return table.getAllFieldNames();
					}
					return table.getAllColumnNames();
				} else {
					String fieldAlias = this.alias;
					if (Strings.isBlank(fieldAlias) && aliasWithField) {
						fieldAlias = field;
					}
					if (Strings.isBlank(fieldAlias)) {
						return Iterables.asList(fieldAlias);
					} else {
						return Iterables.asList(table.getColumnName(field));
					}
				}
			}
		}
		return Collections.emptyList();
	}

	public boolean hasSelectRawColumn(String columnOrAlias) {
		if (sql != null) {
			return false;
		}
		if (value != null && Strings.isNotBlank(alias)) {
			return columnOrAlias.equals(alias);
		}
		if (table != null && Strings.isNotBlank(field)) {
			if (expression != null) {
				String fieldAlias = this.alias;
				if (Strings.isBlank(fieldAlias) && aliasWithField && !field.equals(SymbolConsts.ASTERISK)) {
					fieldAlias = field;
				}
				return columnOrAlias.equals(fieldAlias);
			} else {
				if (field.equals(SymbolConsts.ASTERISK)) {
					if (aliasWithField) {
						return table.getAllFieldNames().contains(columnOrAlias);
					} else {
						return table.getAllColumnNames().contains(columnOrAlias);
					}
				} else {
					String fieldAlias = this.alias;
					if (Strings.isBlank(fieldAlias) && aliasWithField) {
						fieldAlias = field;
					}
					if (Strings.isBlank(fieldAlias)) {
						return columnOrAlias.equals(fieldAlias);
					} else {
						String columnName = table.getColumnName(field);
						return columnOrAlias.equals(columnName);
					}
				}
			}
		}
		return false;
	}


	public O end() {
		return owner;
	}

	public S apply(String function, TableField[] extFields, Object... bindings) {
		return apply(PatternExpression.of(function), extFields, bindings);
	}

	public S apply(String function, TableField... extFields) {
		return apply(PatternExpression.of(function), extFields);
	}

	public S apply(String function) {
		return apply(PatternExpression.of(function), StdConsts.EMPTY_ARRAY);
	}

	public S apply(String function, Object[] bindings) {
		return apply(PatternExpression.of(function), bindings);
	}

	public S apply(Expression function) {
		this.expression = new ExpressionSegment<>(this.expression, function, StdConsts.EMPTY_ARRAY);
		return getThis();
	}

	public S apply(Expression function, Object[] bindings) {
		this.expression = new ExpressionSegment<>(this.expression, function, bindings);
		return getThis();
	}

	public S apply(Expression function, TableField... extFields) {
		return apply(function, extFields, StdConsts.EMPTY_ARRAY);
	}

	public S apply(Expression function, TableField[] extFields, Object... bindings) {
		this.expression = new ExpressionSegment<>(this.expression, tableAccessible, extFields, function, bindings);
		return getThis();
	}

	public S apply(String function, int tableIndex, String field) {
		return apply(function, TableField.of(tableIndex, field));
	}

	public S apply(Expression function, int tableIndex, String field) {
		return apply(function, TableField.of(tableIndex, field));
	}

	public S apply(String function, String tableAlias, String field) {
		return apply(function, TableField.of(tableAlias, field));
	}

	public S apply(Expression function, String tableAlias, String field) {
		return apply(function, TableField.of(tableAlias, field));
	}


	public O value(Object value, String alias) {
		if (Strings.isEmpty(alias)) {
			throw new IllegalArgumentException("别名不能为空");
		}
		this.value = value;
		return alias(alias);
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

	public S all() {
		this.field = SymbolConsts.ASTERISK;
		return getThis();
	}

	public <T, R> S column(GetterFunction<T, R> getter) {
		return column(Reflects.getPropertyName(getter));
	}

	@AnnotationProcessing
	public S column(String field) {
		this.field = field;
		return getThis();
	}

	public S aliasWithField(boolean withFieldAlias) {
		this.aliasWithField = withFieldAlias;
		return getThis();
	}

	public O alias(String alias) {
		this.alias = alias;
		return end();
	}


	public S sql(SqlNode sql) {
		this.sql = sql;
		return getThis();
	}

}

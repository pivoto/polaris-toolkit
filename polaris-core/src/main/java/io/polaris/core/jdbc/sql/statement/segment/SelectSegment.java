package io.polaris.core.jdbc.sql.statement.segment;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.StdConsts;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.VarRef;
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
 * @since Aug 20, 2023
 */
@AnnotationProcessing
public class SelectSegment<O extends Segment<O>, S extends SelectSegment<O, S>> extends BaseSegment<S> implements SqlNodeBuilder, TableAccessibleHolder {
	private final O owner;
	private final TableSegment<? extends TableSegment<?>> table;
	private final TableAccessible tableAccessible;
	private String field;
	private String alias;
	private String aliasPrefix;
	private String aliasSuffix;
	private SqlNode sql;
	private boolean aliasWithField = true;
	/** 固定列值 */
	private Object value;
	/** 固定列值的扩展属性，为如Mybatis等占位符增加配置项 */
	private Map<String, String> props;
	/** 表达式 */
	private ExpressionSegment<?> expression;

	@AnnotationProcessing
	public SelectSegment(O owner, TableSegment<? extends TableSegment<?>> table) {
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
	public TableAccessible getTableAccessible() {
		return tableAccessible;
	}

	@Override
	public SqlNode toSqlNode() {
		return toSqlNode(false);
	}

	static String toAlias(String alias, String aliasPrefix, String aliasSuffix) {
		if (Strings.isBlank(alias)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean startsWith = alias.startsWith("\"");
		boolean endsWith = alias.endsWith("\"");
		if (startsWith) {
			sb.append("\"");
		}
		if (Strings.isNotBlank(aliasPrefix)) {
			sb.append(aliasPrefix);
		}
		sb.append(alias, startsWith ? 1 : 0, endsWith ? alias.length() - 1 : alias.length());
		if (Strings.isNotBlank(aliasSuffix)) {
			sb.append(aliasSuffix);
		}
		if (endsWith) {
			sb.append("\"");
		}
		return sb.toString();
	}

	public SqlNode toSqlNode(boolean quotaAlias) {
		if (sql != null) {
			return sql;
		}
		String fieldAlias = toAlias(this.alias, this.aliasPrefix, this.aliasSuffix);

		if (value != null && Strings.isNotBlank(fieldAlias)) {
			ContainerNode containerNode = new ContainerNode();
			if (props != null && !props.isEmpty()) {
				containerNode.addNode(SqlNodes.dynamic(fieldAlias, VarRef.of(value, props)));
			} else {
				containerNode.addNode(SqlNodes.dynamic(fieldAlias, value));
			}
			if (quotaAlias && !fieldAlias.startsWith("\"")) {
				containerNode.addNode(new TextNode(" \"" + fieldAlias + "\""));
			} else {
				containerNode.addNode(new TextNode(" " + fieldAlias));
			}
			return containerNode;
		}
		if (table != null && Strings.isNotBlank(field)) {
			if (field.equals(SymbolConsts.ASTERISK)) {
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
				return new TextNode(table.getAllColumnExpression(aliasWithField, quotaAlias, aliasPrefix, aliasSuffix));
			} else {
				if (Strings.isBlank(fieldAlias) && aliasWithField) {
					fieldAlias = toAlias(this.field, this.aliasPrefix, this.aliasSuffix);
				}
				String columnExpression = table.getColumnExpression(field);
				if (expression != null) {
					if (Strings.isNotBlank(fieldAlias)) {
						ContainerNode containerNode = new ContainerNode();
						containerNode.addNode(expression.toSqlNode(columnExpression));

						if (quotaAlias && !fieldAlias.startsWith("\"")) {
							containerNode.addNode(new TextNode("  \"" + fieldAlias + "\""));
						} else {
							containerNode.addNode(new TextNode(" " + fieldAlias));
						}
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
					if (Strings.isNotBlank(fieldAlias)) {
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
					if (Strings.isNotBlank(fieldAlias)) {
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

	public S aliasPrefix(String aliasPrefix) {
		this.aliasPrefix = aliasPrefix;
		return getThis();
	}

	public S aliasSuffix(String aliasSuffix) {
		this.aliasSuffix = aliasSuffix;
		return getThis();
	}

	public S aliasWithField() {
		this.aliasWithField = true;
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

	public O value(Object value, String alias) {
		if (Strings.isEmpty(alias)) {
			throw new IllegalArgumentException("别名不能为空");
		}
		if (value instanceof VarRef) {
			this.props = ((VarRef<?>) value).getPropsIfNotEmpty();
			this.value = ((VarRef<?>) value).getValue();
		} else {
			this.value = value;
		}
		return alias(alias);
	}

	public O value(Object value, Map<String, String> props, String alias) {
		if (Strings.isEmpty(alias)) {
			throw new IllegalArgumentException("别名不能为空");
		}
		if (value instanceof VarRef) {
			this.props = props != null ? props : ((VarRef<?>) value).getPropsIfNotEmpty();
			this.value = ((VarRef<?>) value).getValue();
		} else {
			this.value = value;
			this.props = props;
		}
		return alias(alias);
	}


	public S sql(SqlNode sql) {
		this.sql = sql;
		return getThis();
	}

}

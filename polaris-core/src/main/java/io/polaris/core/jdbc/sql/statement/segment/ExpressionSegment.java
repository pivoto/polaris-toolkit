package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.expression.Expression;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
 */
public class ExpressionSegment<S extends ExpressionSegment<S>> extends BaseSegment<S> {

	private final Expression expression;
	private final SqlSourceSupplier extSourcesSupplier;
	private final Object[] bindings;
	private final ExpressionSegment<?> nested;


	public ExpressionSegment(Expression expression, Object... bindings) {
		this(null, SqlSourceSupplier.EMPTY, expression, bindings);
	}

	public ExpressionSegment(SqlSourceSupplier extSourcesSupplier, Expression expression, Object... bindings) {
		this(null, extSourcesSupplier, expression, bindings);
	}

	public ExpressionSegment(ExpressionSegment<?> nested, Expression expression, Object... bindings) {
		this(nested, SqlSourceSupplier.EMPTY, expression, bindings);
	}

	public ExpressionSegment(ExpressionSegment<?> nested, SqlSourceSupplier extSourcesSupplier, Expression expression, Object... bindings) {
		this.nested = nested;
		this.extSourcesSupplier = extSourcesSupplier == null ? SqlSourceSupplier.EMPTY : extSourcesSupplier;
		this.expression = expression;
		this.bindings = bindings;
	}

	public ExpressionSegment(ExpressionSegment<?> nested, TableAccessible tableAccessible, TableField[] extFields, Expression expression, Object... bindings) {
		this.nested = nested;
		if (extFields == null || extFields.length == 0) {
			this.extSourcesSupplier = SqlSourceSupplier.EMPTY;
		} else {
			this.extSourcesSupplier = () -> {
				SqlNode[] sqlNodes = new SqlNode[extFields.length];
				for (int i = 0; i < extFields.length; i++) {
					TableField tableField = extFields[i];
					Integer tableIndex = tableField.getTableIndex();
					if (tableIndex != null) {
						TableSegment<?> table = tableAccessible.getTable(tableIndex);
						String columnExpression = table.getColumnExpression(tableField.getFieldName());
						sqlNodes[i] = new TextNode(columnExpression);
					}else{
						TableSegment<?> table = tableAccessible.getTable(tableField.getTableAlias());
						String columnExpression = table.getColumnExpression(tableField.getFieldName());
						sqlNodes[i] = new TextNode(columnExpression);
					}

				}
				return sqlNodes;
			};
		}

		this.expression = expression;
		this.bindings = bindings;
	}

	public SqlNode toSqlNode(String source) {
		return toSqlNode(new TextNode(source));
	}

	public SqlNode toSqlNode(SqlNode source) {
		if (nested != null) {
			SqlNode sqlNode = nested.toSqlNode(source);
			return expression.toSqlNode(sqlNode, extSourcesSupplier.get(), bindings);
		} else {
			return expression.toSqlNode(source, extSourcesSupplier.get(), bindings);
		}
	}

}

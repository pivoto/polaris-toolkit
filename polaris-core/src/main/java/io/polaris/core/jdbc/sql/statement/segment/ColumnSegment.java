package io.polaris.core.jdbc.sql.statement.segment;

import java.util.Map;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.consts.StdConsts;
import io.polaris.core.jdbc.sql.VarRef;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.Segment;
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
public class ColumnSegment<O extends Segment<O>, S extends ColumnSegment<O, S>> extends BaseSegment<S> {

	private final O owner;
	private final TableSegment<?> table;
	private String field;
	private transient String _rawColumn;
	/** 列值 */
	private Object value;
	/** 列值的扩展属性，为如Mybatis等占位符增加配置项 */
	private String valueProperty;
	/** 表达式 */
	private ExpressionSegment<?> expression;

	public <T extends TableSegment<?>> ColumnSegment(O owner, T table) {
		this.owner = owner;
		this.table = table;
	}

	public O end() {
		return owner;
	}

	public <T, R> S column(GetterFunction<T, R> getter) {
		return column(Reflects.getPropertyName(getter));
	}

	public S column(String field) {
		this.field = field;
		return getThis();
	}

	public S rawColumn(String column) {
		this._rawColumn = column;
		return getThis();
	}

	public S removeProperty() {
		this.valueProperty = null;
		return getThis();
	}

	public S property(String valueProperty) {
		this.valueProperty = Strings.trimToNull(valueProperty);
		return getThis();
	}

	public S value(Object value, String valueProperty) {
		this.value = value;
		this.valueProperty = Strings.trimToNull(valueProperty);
		return getThis();
	}

	public S value(Object value) {
		this.value = value;
		return getThis();
	}

	public S rawValue(String text) {
		this.value = SqlNodes.text(text);
		return getThis();
	}

	public S apply(String functionPattern, String[] extFieldNames, Map<String, Object> bindings) {
		return apply(Expressions.pattern(functionPattern), extFieldNames, bindings);
	}

	public S apply(String functionPattern, String[] extFieldNames, Object... bindings) {
		return apply(Expressions.pattern(functionPattern), extFieldNames, bindings);
	}

	public S apply(String functionPattern) {
		return apply(Expressions.pattern(functionPattern));
	}

	public S apply(String functionPattern, Object[] bindings) {
		return apply(Expressions.pattern(functionPattern), bindings);
	}

	public S apply(String functionPattern, Map<String, Object> bindings) {
		return apply(Expressions.pattern(functionPattern), bindings);
	}

	public S apply(Expression function, String[] extFieldNames, Map<String, Object> bindings) {
		TableField[] extFields = extFieldNames == null ? new TableField[0] : new TableField[extFieldNames.length];
		for (int i = 0; i < extFields.length; i++) {
			extFields[i] = TableField.of(0, extFieldNames[i]);
		}
		this.expression = new ExpressionSegment<>(this.expression, this.table.toTableAccessible(), extFields, function, bindings);
		return getThis();
	}

	public S apply(Expression function, String[] extFieldNames, Object... bindings) {
		TableField[] extFields = extFieldNames == null ? new TableField[0] : new TableField[extFieldNames.length];
		for (int i = 0; i < extFields.length; i++) {
			extFields[i] = TableField.of(0, extFieldNames[i]);
		}
		this.expression = new ExpressionSegment<>(this.expression, this.table.toTableAccessible(), extFields, function, bindings);
		return getThis();
	}

	public S apply(Expression function) {
		this.expression = new ExpressionSegment<>(this.expression, function, StdConsts.EMPTY_ARRAY);
		return getThis();
	}

	public S apply(Expression function, Object[] bindings) {
		this.expression = new ExpressionSegment<>(this.expression, function, bindings);
		return getThis();
	}

	public S apply(Expression function, Map<String, Object> bindings) {
		this.expression = new ExpressionSegment<>(this.expression, function, bindings);
		return getThis();
	}

	public TableSegment<?> getTable() {
		return table;
	}

	public SqlNode toValueSqlNode() {
		if (this.expression != null) {
			return this.expression.toSqlNode(name());
		}
		Object columnValue = this.value;
		if (columnValue instanceof SqlNode) {
			return (SqlNode) columnValue;
		}
		if (columnValue == null) {
			return SqlNodes.mixed(name(), null);
		} else {
			if (valueProperty != null) {
				return SqlNodes.dynamic(name(), VarRef.of(columnValue, valueProperty));
			} else {
				return SqlNodes.dynamic(name(), columnValue);
			}
		}
	}

	public String getColumnName() {
		return name();
	}

	private String name() {
		if (Strings.isNotBlank(_rawColumn)) {
			return _rawColumn;
		}
		this._rawColumn = table.getColumnName(field);
		return _rawColumn;
	}

}

package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.reflect.GetterFunction;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
public class ColumnSegment<S extends ColumnSegment<S>> extends BaseSegment<S> {

	private final TableSegment<?> table;
	private String field;
	private transient String _rawColumn;
	private Object value;

	public <T extends TableSegment<?>> ColumnSegment(T table) {
		this.table = table;
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

	public S value(Object value) {
		this.value = value;
		return getThis();
	}

	public TableSegment<?> getTable() {
		return table;
	}

	public Object getColumnValue() {
		return value;
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

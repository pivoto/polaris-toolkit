package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.SqlTextParsers;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.reflect.GetterFunction;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
@AnnotationProcessing
public class GroupBySegment<O extends Segment<O>, S extends GroupBySegment<O, S>> extends BaseSegment<S> implements SqlNodeBuilder {
	private final O owner;
	private final TableSegment<?> table;
	private final TableAccessible tableAccessible;
	private String field;
	private transient String _rawColumn;
	private SqlNode sql;

	@AnnotationProcessing
	public GroupBySegment(O owner, TableSegment<?> table) {
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
		ContainerNode sql = new ContainerNode();
		sql.addNode(new TextNode(column));
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
		// 解析表字段名
		rawColumn = SqlTextParsers.resolveRefTableField(rawColumn, tableAccessible);
		this._rawColumn = rawColumn;
		return getThis();
	}

	public TableSegment<?> getTable() {
		return table;
	}
}

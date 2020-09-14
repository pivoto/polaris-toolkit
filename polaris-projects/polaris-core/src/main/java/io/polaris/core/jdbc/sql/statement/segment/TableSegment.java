package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
public abstract class TableSegment<S extends TableSegment<S>> extends BaseSegment<S> implements SqlNodeBuilder {

	protected TableSegment() {
	}

	public abstract SqlNode toSqlNode(boolean withAlias);

	@Override
	public SqlNode toSqlNode() {
		return toSqlNode(true);
	}

	/**
	 * 只支持{@linkplain TableEntitySegment}类型，否则返回 null
	 *
	 * @return
	 */
	@Nullable
	public TableMeta getTableMeta() {
		return null;
	}

	public abstract String getTableAlias();

	/**
	 * 返回所有的含表别名前缀的列名表达式，逗号分隔
	 */
	public String getAllColumnExpression(boolean quotaAlias) {
		return getAllColumnExpression(false, false);
	}

	/**
	 * 返回所有的含表别名前缀的列名表达式，逗号分隔，并使用实体字段名作为列的别名
	 */
	public abstract String getAllColumnExpression(boolean aliasWithField, boolean quotaAlias);

	/**
	 * 返回含表别名前缀的列名表达式
	 */
	public abstract String getColumnExpression(String field);

	public abstract List<String> getAllColumnNames();

	public abstract List<String> getAllFieldNames();

	/**
	 * 返回对应的列名
	 */
	public abstract String getColumnName(String field);

}

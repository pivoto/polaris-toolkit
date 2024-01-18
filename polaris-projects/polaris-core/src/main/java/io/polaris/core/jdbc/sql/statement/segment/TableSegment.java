package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.SetOpsStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
@AnnotationProcessing
public abstract class TableSegment<S extends TableSegment<S>> extends BaseSegment<S> implements SqlNodeBuilder {

	protected TableSegment() {
	}

	public static TableSegment<?> fromEntity(Class<?> entityClass, String alias) {
		return new TableEntitySegment<>(entityClass, alias);
	}

	public static TableSegment<?> fromSelect(SelectStatement<?> select, String alias) {
		return new TableViewSegment<>(select, alias);
	}

	public static TableSegment<?> fromSetOps(SetOpsStatement<?> select, String alias) {
		return new TableSetViewSegment<>(select, alias);
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
		return getAllColumnExpression(false, false, "", "");
	}

	/**
	 * 返回所有的含表别名前缀的列名表达式，逗号分隔，并使用实体字段名作为列的别名
	 */
	public abstract String getAllColumnExpression(boolean aliasWithField, boolean quotaAlias, String aliasPrefix, String aliasSuffix);

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

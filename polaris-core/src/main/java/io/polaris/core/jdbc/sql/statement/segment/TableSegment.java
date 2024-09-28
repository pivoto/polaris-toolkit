package io.polaris.core.jdbc.sql.statement.segment;

import java.util.List;

import javax.annotation.Nullable;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.SetOpsStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Aug 20, 2023
 */
@AnnotationProcessing
public abstract class TableSegment<S extends TableSegment<S>> extends BaseSegment<S> implements SqlNodeBuilder {

	private TableAccessible tableAccessible = new TableAccessible() {
		@Override
		public TableSegment<?> getTable(int tableIndex) {
			if (tableIndex == 0) {
				return TableSegment.this;
			}
			return null;
		}

		@Override
		public TableSegment<?> getTable(String tableAlias) {
			if (Strings.equals(getTableAlias(), tableAlias)) {
				return TableSegment.this;
			}
			return null;
		}
	};

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

	public TableAccessible toTableAccessible() {
		return tableAccessible;
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

	/** 返回所有的含表别名前缀的列名表达式，逗号分隔 */
	public String getAllColumnExpression(boolean quotaAlias) {
		return getAllColumnExpression(true, quotaAlias);
	}

	/** 返回所有的含表别名前缀的列名表达式，逗号分隔 */
	public String getAllColumnExpression(boolean withTableAlias, boolean quotaAlias) {
		return getAllColumnExpression(withTableAlias, false, quotaAlias, "", "");
	}

	/**
	 * 返回所有的含表别名前缀的列名表达式，逗号分隔，使用实体字段名作为列的别名时同时添加前缀和后缀
	 */
	public String getAllColumnExpression(boolean aliasWithField, boolean quotaAlias, String aliasPrefix, String aliasSuffix) {
		return getAllColumnExpression(true, aliasWithField, quotaAlias, aliasPrefix, aliasSuffix);
	}

	/**
	 * 返回所有的含表别名前缀的列名表达式，逗号分隔，使用实体字段名作为列的别名时同时添加前缀和后缀
	 */
	public abstract String getAllColumnExpression(boolean withTableAlias, boolean aliasWithField, boolean quotaAlias, String aliasPrefix, String aliasSuffix);

	/**
	 * 返回含表别名前缀的列名表达式
	 */
	public String getColumnExpression(String field) {
		return getColumnExpression(field, true);
	}

	/**
	 * 返回含表别名前缀的列名表达式
	 */
	public abstract String getColumnExpression(String field, boolean withTableAlias);

	public abstract List<String> getAllColumnNames();

	public abstract List<String> getAllFieldNames();

	/**
	 * 返回对应的列名
	 */
	public abstract String getColumnName(String field);

}

package io.polaris.core.jdbc.sql.node;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
 */
public class SqlNodeOps {

	private final SqlNode sqlNode;
	private boolean deleted;
	private SqlNode replaced;

	public SqlNodeOps(SqlNode sqlNode) {
		this.sqlNode = sqlNode;
	}

	public void delete() {
		this.deleted = true;
	}

	public void replace(SqlNode replaced) {
		this.replaced = replaced;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public boolean isReplaced() {
		return replaced != null;
	}

	public SqlNode getReplaced() {
		return replaced;
	}

	public SqlNode getSqlNode() {
		return sqlNode;
	}
}

package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.jdbc.sql.node.SqlNode;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
public interface SqlNodeBuilder {

	SqlNode toSqlNode();
}

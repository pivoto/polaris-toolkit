package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.jdbc.sql.node.SqlNode;

import java.util.function.Supplier;

/**
 * @author Qt
 * @since  Aug 23, 2023
 */
@FunctionalInterface
public interface SqlSourceSupplier extends Supplier<SqlNode[]> {

	SqlSourceSupplier EMPTY = () -> new SqlNode[0];

	static SqlSourceSupplier from(Supplier<SqlNode> supplier) {
		return () -> new SqlNode[]{supplier.get()};
	}
}

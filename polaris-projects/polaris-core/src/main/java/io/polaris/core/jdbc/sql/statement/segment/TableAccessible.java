package io.polaris.core.jdbc.sql.statement.segment;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
 */
public interface TableAccessible extends TableAccessibleHolder {

	TableSegment<?> getTable(int tableIndex);

	TableSegment<?> getTable(String tableAlias);

	@Override
	default TableAccessible getTableAccessible() {
		return this;
	}
}

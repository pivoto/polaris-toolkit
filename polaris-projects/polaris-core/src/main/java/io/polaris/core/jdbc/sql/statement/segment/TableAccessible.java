package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.string.Strings;

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

	static TableAccessible of(TableSegment<?>... tables) {
		return new TableAccessible() {
			@Override
			public TableSegment<?> getTable(int tableIndex) {
				return tables.length > tableIndex ? tables[tableIndex] : null;
			}

			@Override
			public TableSegment<?> getTable(String tableAlias) {
				for (int i = 0; i < tables.length; i++) {
					TableSegment<?> table = tables[i];
					if (Strings.equals(table.getTableAlias(), tableAlias)) {
						return table;
					}
				}
				return null;
			}
		};
	}
}

package io.polaris.core.jdbc.sql.statement;

/**
 * @author Qt
 * @since 1.8,  Jan 29, 2024
 */
public class DefaultColumnPredicate implements ColumnPredicate {
	private final boolean includeAllEmptyColumns;

	DefaultColumnPredicate(boolean includeAllEmptyColumns) {
		this.includeAllEmptyColumns = includeAllEmptyColumns;
	}

	@Override
	public boolean isIncludedColumn(String name) {
		return true;
	}

	@Override
	public boolean isIncludedEmptyColumn(String name) {
		return includeAllEmptyColumns;
	}
}

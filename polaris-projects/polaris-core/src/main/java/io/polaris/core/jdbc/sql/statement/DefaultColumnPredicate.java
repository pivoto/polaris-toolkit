package io.polaris.core.jdbc.sql.statement;

/**
 * @author Qt
 * @since 1.8,  Jan 29, 2024
 */
public class DefaultColumnPredicate implements ColumnPredicate {
	public static final DefaultColumnPredicate INSTANCE = new DefaultColumnPredicate();

	private DefaultColumnPredicate() {
	}


	@Override
	public boolean isIncludedColumn(String name) {
		return true;
	}

	@Override
	public boolean isIncludedEmptyColumn(String name) {
		return false;
	}
}

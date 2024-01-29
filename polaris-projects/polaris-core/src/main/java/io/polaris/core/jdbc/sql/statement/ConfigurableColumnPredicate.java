package io.polaris.core.jdbc.sql.statement;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import io.polaris.core.collection.Iterables;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8,  Jan 28, 2024
 */
public class ConfigurableColumnPredicate implements ColumnPredicate {
	private final Predicate<String> isIncludeColumns;
	private final Predicate<String> isExcludeColumns;
	private final Predicate<String> isIncludeEmptyColumns;
	private final boolean includeAllEmpty;

	private ConfigurableColumnPredicate(Predicate<String> isIncludeColumns, Predicate<String> isExcludeColumns, Predicate<String> isIncludeEmptyColumns, boolean includeAllEmpty) {
		this.isIncludeColumns = isIncludeColumns;
		this.isExcludeColumns = isExcludeColumns;
		this.isIncludeEmptyColumns = isIncludeEmptyColumns;
		this.includeAllEmpty = includeAllEmpty;
	}

	public static ColumnPredicate of(Predicate<String> isIncludeColumns, Predicate<String> isExcludeColumns, Predicate<String> isIncludeEmptyColumns, boolean includeAllEmpty) {
		return new ConfigurableColumnPredicate(isIncludeColumns, isExcludeColumns, isIncludeEmptyColumns, includeAllEmpty);
	}

	public static ColumnPredicate of(String[] includeColumns, String[] excludeColumns, String[] includeEmptyColumns, boolean includeAllEmpty) {
		return new ConfigurableColumnPredicate(
			toPredicate(includeColumns),
			toPredicate(excludeColumns),
			toPredicate(includeEmptyColumns),
			includeAllEmpty);
	}
	public static ColumnPredicate of(Predicate<String> isIncludeEmptyColumns) {
		return new ConfigurableColumnPredicate(null, null, isIncludeEmptyColumns, false);
	}

	public static ColumnPredicate of(String[] includeEmptyColumns) {
		return new ConfigurableColumnPredicate(null, null, toPredicate(includeEmptyColumns), false);
	}


	public static ColumnPredicate of(Map<String, Object> bindings, String[] includeColumns, String includeColumnsKey, String[] excludeColumns, String excludeColumnsKey, String[] includeEmptyColumns, String includeEmptyColumnsKey, boolean includeAllEmpty, String includeAllEmptyKey) {
		Predicate<String> isIncludeColumns = toPredicate(bindings, includeColumns, includeColumnsKey);
		Predicate<String> isExcludeColumns = toPredicate(bindings, excludeColumns, excludeColumnsKey);
		Predicate<String> isIncludeEmptyColumns = toPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		includeAllEmpty = toBoolean(bindings, includeAllEmpty, includeAllEmptyKey);
		return new ConfigurableColumnPredicate(isIncludeColumns, isExcludeColumns, isIncludeEmptyColumns, includeAllEmpty);
	}


	public static boolean toBoolean(Map<String, Object> bindings, boolean includeAllEmpty, String includeAllEmptyKey) {
		if (!includeAllEmpty) {
			if (Strings.isNotBlank(includeAllEmptyKey)) {
				Object val = EntityStatements.getObjectOfKey(bindings, includeAllEmptyKey, null);
				if (val instanceof Boolean) {
					includeAllEmpty = ((Boolean) val).booleanValue();
				}
			}
		}
		return includeAllEmpty;
	}

	public static Predicate<String> toPredicate(String[] columns) {
		if (columns == null || columns.length == 0) {
			return null;
		}
		Set<String> set = Iterables.asSet(columns);
		return set::contains;
	}

	public static Predicate<String> toPredicate(Map<String, Object> bindings, String[] columns, String keyColumns) {
		Predicate<String> predicate = null;
		if (columns == null || columns.length == 0) {
			if (Strings.isNotBlank(keyColumns)) {
				Object val = EntityStatements.getObjectOfKey(bindings, keyColumns, null);
				if (val instanceof String[]) {
					columns = (String[]) val;
				}
			}
		}
		if (columns != null && columns.length > 0) {
			Set<String> set = Iterables.asSet(columns);
			predicate = set::contains;
		}
		return predicate;
	}

	@Override
	public boolean isIncludedColumn(String name) {
		// 不在包含列表
		if (isIncludeColumns != null && !isIncludeColumns.test(name)) {
			return false;
		}
		// 在排除列表
		if (isExcludeColumns != null && isExcludeColumns.test(name)) {
			return false;
		}
		return true;
	}


	@Override
	public boolean isIncludedEmptyColumn(String name) {
		// 需要包含空值字段
		return includeAllEmpty || (isIncludeEmptyColumns != null && isIncludeEmptyColumns.test(name));
	}
}

package io.polaris.core.jdbc.sql;

import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since 1.8,  Jan 28, 2024
 */
public class ColumnPredicate {
	private final Predicate<String> isIncludeColumns;
	private final Predicate<String> isExcludeColumns;
	private final Predicate<String> isIncludeEmptyColumns;
	private final boolean includeAllEmpty;

	public ColumnPredicate(Map<String, Object> bindings,
		String[] includeColumns, String includeColumnsKey,
		String[] excludeColumns, String excludeColumnsKey,
		String[] includeEmptyColumns, String includeEmptyColumnsKey,
		boolean includeAllEmpty, String includeAllEmptyKey) {
		Predicate<String> isIncludeColumns = EntityStatements.getColumnPredicate(bindings, includeColumns, includeColumnsKey);
		Predicate<String> isExcludeColumns = EntityStatements.getColumnPredicate(bindings, excludeColumns, excludeColumnsKey);
		Predicate<String> isIncludeEmptyColumns = EntityStatements.getColumnPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		includeAllEmpty = EntityStatements.isIncludeEmpty(bindings, includeAllEmpty, includeAllEmptyKey);
		this.isIncludeColumns = isIncludeColumns;
		this.isExcludeColumns = isExcludeColumns;
		this.isIncludeEmptyColumns = isIncludeEmptyColumns;
		this.includeAllEmpty = includeAllEmpty;
	}

	public ColumnPredicate(
		Predicate<String> isIncludeColumns,
		Predicate<String> isExcludeColumns,
		Predicate<String> isIncludeEmptyColumns,
		boolean includeAllEmpty) {
		this.isIncludeColumns = isIncludeColumns;
		this.isExcludeColumns = isExcludeColumns;
		this.isIncludeEmptyColumns = isIncludeEmptyColumns;
		this.includeAllEmpty = includeAllEmpty;
	}

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


	public boolean isIncludedEmptyColumn(String name) {
		// 需要包含空值字段
		return includeAllEmpty || (isIncludeEmptyColumns != null && isIncludeEmptyColumns.test(name));
	}

	static Object getObjectOfKey(Map<String, Object> bindings, String key, Object defVal) {
		return bindings == null ? defVal : bindings.getOrDefault(key, defVal);
	}
}

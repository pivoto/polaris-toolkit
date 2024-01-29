package io.polaris.core.jdbc.sql.statement;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import io.polaris.core.collection.Iterables;
import io.polaris.core.string.Strings;

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
		this.isIncludeColumns = getColumnPredicate(bindings, includeColumns, includeColumnsKey);
		this.isExcludeColumns = getColumnPredicate(bindings, excludeColumns, excludeColumnsKey);
		this.isIncludeEmptyColumns = getColumnPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		this.includeAllEmpty = isIncludeEmpty(bindings, includeAllEmpty, includeAllEmptyKey);
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

	static boolean isIncludeEmpty(Map<String, Object> bindings, boolean includeAllEmpty, String includeAllEmptyKey) {
		if (!includeAllEmpty) {
			if (Strings.isNotBlank(includeAllEmptyKey)) {
				Object val = getObjectOfKey(bindings, includeAllEmptyKey, null);
				if (val instanceof Boolean) {
					includeAllEmpty = ((Boolean) val).booleanValue();
				}
			}
		}
		return includeAllEmpty;
	}

	static Predicate<String> getColumnPredicate(Map<String, Object> bindings, String[] columns, String keyColumns) {
		Predicate<String> predicate = null;
		if (columns == null || columns.length == 0) {
			if (Strings.isNotBlank(keyColumns)) {
				Object val = getObjectOfKey(bindings, keyColumns, null);
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
	static Object getObjectOfKey(Map<String, Object> bindings, String key, Object defVal) {
		return bindings == null ? defVal : bindings.getOrDefault(key, defVal);
	}
}

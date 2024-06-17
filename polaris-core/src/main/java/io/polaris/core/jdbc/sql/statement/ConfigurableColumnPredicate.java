package io.polaris.core.jdbc.sql.statement;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import io.polaris.core.collection.Iterables;
import io.polaris.core.jdbc.sql.BindingValues;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Jan 28, 2024
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

	public static ColumnPredicate of(Map<String, Object> bindings, String[] includeColumns, String[] includeColumnsKey, String[] excludeColumns, String[] excludeColumnsKey, String[] includeEmptyColumns, String[] includeEmptyColumnsKey, boolean includeAllEmpty, String[] includeAllEmptyKey) {
		Predicate<String> isIncludeColumns = toPredicate(bindings, includeColumns, includeColumnsKey);
		Predicate<String> isExcludeColumns = toPredicate(bindings, excludeColumns, excludeColumnsKey);
		Predicate<String> isIncludeEmptyColumns = toPredicate(bindings, includeEmptyColumns, includeEmptyColumnsKey);
		includeAllEmpty = toBoolean(bindings, includeAllEmpty, includeAllEmptyKey);
		return new ConfigurableColumnPredicate(isIncludeColumns, isExcludeColumns, isIncludeEmptyColumns, includeAllEmpty);
	}

	public static ColumnPredicate of(Map<String, Object> bindings, io.polaris.core.jdbc.annotation.segment.ColumnPredicate predicate) {
		return ConfigurableColumnPredicate.of(bindings,
			predicate.includeColumns(), predicate.includeColumnsKey(),
			predicate.excludeColumns(), predicate.excludeColumnsKey(),
			predicate.includeEmptyColumns(), predicate.includeEmptyColumnsKey(),
			predicate.includeAllEmpty(), predicate.includeAllEmptyKey());
	}


	public static boolean toBoolean(Map<String, Object> bindings, boolean includeAllEmpty, String includeAllEmptyKey) {
		if (!includeAllEmpty) {
			if (Strings.isNotBlank(includeAllEmptyKey)) {
				Object val = BindingValues.getBindingValueOrDefault(bindings, includeAllEmptyKey, null);
				if (val instanceof Boolean) {
					includeAllEmpty = ((Boolean) val).booleanValue();
				}
			}
		}
		return includeAllEmpty;
	}

	public static boolean toBoolean(Map<String, Object> bindings, boolean includeAllEmpty, String[] includeAllEmptyKeys) {
		if (!includeAllEmpty) {
			if (includeAllEmptyKeys != null) {
				for (String includeAllEmptyKey : includeAllEmptyKeys) {
					if (Strings.isNotBlank(includeAllEmptyKey)) {
						Object val = BindingValues.getBindingValueOrDefault(bindings, includeAllEmptyKey, null);
						if (val instanceof Boolean) {
							includeAllEmpty = ((Boolean) val).booleanValue();
							break;
						}
					}
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

	@SuppressWarnings("unchecked")
	public static Predicate<String> toPredicate(Map<String, Object> bindings, String[] columns, String columnsKey) {
		// 忽略空数组或空集合
		if (columns != null && columns.length > 0) {
			Set<String> set = Iterables.asSet(columns);
			return set::contains;
		}
		if (Strings.isNotBlank(columnsKey)) {
			Object val = BindingValues.getBindingValueOrDefault(bindings, columnsKey, null);
			if (val instanceof Set) {
				if (!((Set<String>) val).isEmpty()) {
					return ((Set<String>) val)::contains;
				}
			} else if (val instanceof String[]) {
				if (((String[]) val).length > 0) {
					Set<String> set = Iterables.asSet(columns);
					return set::contains;
				}
			} else if (val instanceof Collection) {
				Set<String> set = Iterables.asSet((Collection<String>) val);
				if (!set.isEmpty()) {
					return set::contains;
				}
			} else if (val instanceof Iterator) {
				Set<String> set = Iterables.asSet((Iterator<String>) val);
				if (!set.isEmpty()) {
					return set::contains;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Predicate<String> toPredicate(Map<String, Object> bindings, String[] columns, String[] columnsKeys) {
		// 忽略空数组或空集合
		if (columns != null && columns.length > 0) {
			Set<String> set = Iterables.asSet(columns);
			return set::contains;
		}
		if (columnsKeys != null) {
			for (String columnsKey : columnsKeys) {
				if (Strings.isNotBlank(columnsKey)) {
					Object val = BindingValues.getBindingValueOrDefault(bindings, columnsKey, null);
					if (val instanceof Set) {
						if (!((Set<String>) val).isEmpty()) {
							return ((Set<String>) val)::contains;
						}
					} else if (val instanceof String[]) {
						if (((String[]) val).length > 0) {
							Set<String> set = Iterables.asSet(columns);
							return set::contains;
						}
					} else if (val instanceof Iterable) {
						Set<String> set = Iterables.asSet((Iterable<String>) val);
						if (!set.isEmpty()) {
							return set::contains;
						}
					} else if (val instanceof Iterator) {
						Set<String> set = Iterables.asSet((Iterator<String>) val);
						if (!set.isEmpty()) {
							return set::contains;
						}
					}
				}
			}
		}
		return null;
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

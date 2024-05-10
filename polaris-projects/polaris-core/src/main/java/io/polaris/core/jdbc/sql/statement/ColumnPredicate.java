package io.polaris.core.jdbc.sql.statement;

/**
 * @author Qt
 * @since  Jan 28, 2024
 */
public interface ColumnPredicate {

	ColumnPredicate DEFAULT = new DefaultColumnPredicate(false);
	ColumnPredicate ALL = new DefaultColumnPredicate(true);

	/**
	 * 判断字段是否被包含在处理序列
	 *
	 * @param name 字段名
	 * @return 是否被包含
	 */
	boolean isIncludedColumn(String name);

	/**
	 * 判断字段是否被包含在空值处理序列
	 *
	 * @param name 字段名
	 * @return 是否被包含
	 */
	boolean isIncludedEmptyColumn(String name);


}

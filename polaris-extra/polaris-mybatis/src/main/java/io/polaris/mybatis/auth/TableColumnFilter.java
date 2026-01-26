package io.polaris.mybatis.auth;

import net.sf.jsqlparser.schema.Table;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
public interface TableColumnFilter {

	boolean apply(Table table, String columnName);

}

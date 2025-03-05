package io.polaris.core.jdbc.dialect;

import io.polaris.core.jdbc.sql.query.Pageable;

/**
 * @author Qt
 * @since Mar 05, 2025
 */
public interface Dialect {

	String getPageSql(String sql, int pageNum, int pageSize);

	default String getPageSql(String sql, Pageable pageable) {
		return getPageSql(sql, pageable.getPageNum(), pageable.getPageSize());
	}

}

package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.polaris.core.jdbc.sql.query.Pageable;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ResultExtractor<T> {

	T extract(ResultSet rs) throws SQLException;

	/** 使用内存分页时调用 */
	default T extract(ResultSet rs, Pageable pageable) throws SQLException {
		return extract(new MemoryPageableResultSet(rs, pageable));
	}

}

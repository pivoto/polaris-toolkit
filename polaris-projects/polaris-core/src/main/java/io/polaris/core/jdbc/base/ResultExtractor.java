package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface ResultExtractor<T> {

	T extract(ResultSet rs) throws SQLException;

}

package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8
 */
public interface ResultExtractor<T> {

	T visit(ResultSet rs) throws SQLException;

}

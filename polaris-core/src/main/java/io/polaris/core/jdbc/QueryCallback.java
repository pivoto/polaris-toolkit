package io.polaris.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8
 */
public interface QueryCallback<T> {

	T visit(ResultSet rs) throws SQLException;

}

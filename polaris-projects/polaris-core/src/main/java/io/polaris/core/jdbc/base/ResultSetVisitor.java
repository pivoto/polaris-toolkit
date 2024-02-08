package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
@FunctionalInterface
public interface ResultSetVisitor {

	void visit(ResultSet rs) throws SQLException;

}

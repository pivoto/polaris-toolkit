package io.polaris.core.jdbc.base;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8,  Apr 25, 2024
 */
public interface ParameterPreparer {

	void set(PreparedStatement st, int index, Object value) throws SQLException;

}

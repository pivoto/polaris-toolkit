package io.polaris.core.jdbc.base;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8,  Dec 28, 2023
 */
public interface StatementPreparer {

	void setParameters(PreparedStatement st, ParameterPreparer parameterPreparer) throws SQLException;

}

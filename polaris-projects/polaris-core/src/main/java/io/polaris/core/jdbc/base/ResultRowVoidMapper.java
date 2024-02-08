package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
public class ResultRowVoidMapper extends BaseResultRowMapper<Void> {

	@Override
	public Void map(ResultSet rs, String[] columns) throws SQLException {
		return null;
	}

}

package io.polaris.core.jdbc.base;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8,  Dec 28, 2023
 */
public interface PreparedStatementSetting {

	void set(PreparedStatement pstmt) throws SQLException;

	static PreparedStatementSetting allOf(PreparedStatementSetting... settings) {
		return pstmt -> {
			for (PreparedStatementSetting setting : settings) {
				setting.set(pstmt);
			}
		};
	}
}

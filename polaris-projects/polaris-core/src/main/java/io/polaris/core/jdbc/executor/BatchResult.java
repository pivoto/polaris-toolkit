package io.polaris.core.jdbc.executor;

import lombok.Getter;

/**
 * @author Qt
 * @since  Feb 07, 2024
 */
@Getter
public class BatchResult {
	private final String sql;
	private final int[] rows;

	public BatchResult(String sql, int[] rows) {
		this.sql = sql;
		this.rows = rows;
	}
}

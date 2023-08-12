package io.polaris.core.jdbc.sql.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
@Getter
@Setter
@NoArgsConstructor
public class RowsResult {

	private Map<String, String> label;
	private List<Map<String, Object>> rows;

	public RowsResult(Map<String, String> label, List<Map<String, Object>> rows) {
		this.label = label;
		this.rows = rows;
	}
}

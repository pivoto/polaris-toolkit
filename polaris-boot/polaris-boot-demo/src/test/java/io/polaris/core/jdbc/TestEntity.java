package io.polaris.core.jdbc;

import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Expression;
import io.polaris.core.jdbc.annotation.Table;
import lombok.Data;

/**
 * @author Qt
 * @since Sep 27, 2024
 */
@Data
@Table(value = "t_demo")
public class TestEntity {
	private long id;
	private String name;
	private double score;
	@Column("field_str")
	private String fieldStr1;
	private String fieldStr2;
	private String fieldStr3;

	@Column(ignored = true)
	private String aId;

	@Expression(value = "id + 1", selectable = true)
	private long id2;
}

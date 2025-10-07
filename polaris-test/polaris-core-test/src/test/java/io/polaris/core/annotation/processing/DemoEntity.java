package io.polaris.core.annotation.processing;

import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Table;
import lombok.Data;

/**
 * @author Qt
 * @since  Aug 20, 2023
 */
@Data
@Table(value = "t_demo")
public class DemoEntity  {
	private long id;
	private String name;
	private double score;
	@Column("field_str")
	private String fieldStr1;
	private String fieldStr2;
	private String fieldStr3;

	@Column(ignored = true)
	private String aId;
}

package io.polaris.core.jdbc.entity;

import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
@Table(value = "t_demo2")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class Demo3Entity {
	@Id
	private Long id;
	private String name;
	private Double score;
	@Column("FIELD_STR")
	private String fieldStr1;
	private String fieldStr2;
	private String fieldStr3;
	@Column("\"col1\"")
	private Integer col1;
	private Integer col2;
	private Integer col3;
	private Integer col4;
	private Integer col5;
}

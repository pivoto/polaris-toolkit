package io.polaris.core.jdbc.entity;

import java.util.Date;

import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since 1.8,  Feb 08, 2024
 */
@Data
@Table("DEMO_TEST02")
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemoTest02Entity {
	@Id
	private Long id;
	private String name;
	private Integer age;
	private String sex;
	private String intro;
	private Boolean deleted;
	private Long version;
	private Date crtDt;
	private Date uptDt;
}

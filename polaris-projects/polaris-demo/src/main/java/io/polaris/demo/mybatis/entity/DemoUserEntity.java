package io.polaris.demo.mybatis.entity;

import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
@Data
@Table(value = "demo_user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemoUserEntity {
	@Id
	@Column
	private Long id;
	private String name;
	private Integer age;
	private String intro;
	@Column(version = true)
	private Long version;
	@Column(logicDeleted = true, insertDefault = "0", updateDefault = "0")
	private Boolean deleted;
	@Column(createTime = true)
	private Date crtDt;
	@Column(createTime = true, updateTime = true)
	private Date uptDt;
}

package io.polaris.core.lang;

import java.util.Date;

import io.polaris.core.annotation.Access;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since 1.8,  May 06, 2024
 */
@Access(
	fields = true,
	fluent = true,
	map = true,
	getters = true,
	setters = true
)
@Getter
@Setter
@FieldNameConstants
public class AccessDemoBean {
	private String id;
	private String name;
	private Integer age;
	private Boolean sex;
	private String remark;
	private Date createTime;
	private Date updateTime;
}

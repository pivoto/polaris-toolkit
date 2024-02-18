package io.polaris.demo.mybatis.entity;

import java.util.Date;

import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Qt
 * @since 1.8,  Jan 30, 2024
 */
@Data
@Table(value = "demo_user_org")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemoUserOrgEntity {
	@Id
	@Column
	private Long id;
	@Column(value = "user_id")
	private String userId;
	@Column(value = "org_id")
	private String orgId;
	@Column(version = true)
	private Long version;
	@Column(logicDeleted = true, insertDefault = "0", updateDefault = "0")
	private Boolean deleted;
	@Column(createTime = true)
	private Date crtDt;
	@Column(createTime = true, updateTime = true)
	private Date uptDt;

	@Column(ignored = true)
	private DemoOrgEntity org;
	@Column(ignored = true)
	private DemoUserEntity user;
}

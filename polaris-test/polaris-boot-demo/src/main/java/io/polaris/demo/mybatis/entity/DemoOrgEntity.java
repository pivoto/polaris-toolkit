package io.polaris.demo.mybatis.entity;

import java.util.Date;

import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import io.polaris.mybatis.annotation.ColumnTypeHandler;
import io.polaris.mybatis.type.DynamicDateTypeHandler;
import io.polaris.mybatis.type.DynamicTimestampTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
@Data
@Table(value = "demo_org")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemoOrgEntity {
	@Id(seqName = "SEQ_DEMO_ORG")
	@Column
	private Long id;
	private String name;
	private String intro;
	@Column(version = true)
	private Long version;
	@Column(logicDeleted = true, insertDefault = "0", updateDefault = "0")
	private Boolean deleted;
	@Column(createTime = true)
	@ColumnTypeHandler(DynamicTimestampTypeHandler.class)
	private Date crtDt;
	@Column(createTime = true, updateTime = true)
	@ColumnTypeHandler(DynamicTimestampTypeHandler.class)
	private Date uptDt;
}

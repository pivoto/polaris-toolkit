package io.polaris.demo.mybatis.entity;

import java.util.Date;

import javax.annotation.Nonnull;

import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.demo.annotation.DemoOrgTable;
import io.polaris.mybatis.annotation.ColumnJdbcType;
import io.polaris.mybatis.annotation.ColumnTypeHandler;
import io.polaris.mybatis.consts.DbEnum;
import io.polaris.mybatis.type.DynamicTimestampTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
@Data
//@Table(value = "demo_org")
@DemoOrgTable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemoOrgEntity {
	@Id(seqName = "SEQ_DEMO_ORG")
	@Column
	private Long id;
	private String name;
	private String intro;
	private OrgType orgType;
	@Column(version = true)
	private Long version;
	@Column(logicDeleted = true, insertDefault = "0", updateDefault = "0")
	private Boolean deleted;
	@Column(createTime = true)
	@ColumnTypeHandler(DynamicTimestampTypeHandler.class)
	@ColumnJdbcType(JdbcType.TIMESTAMP)
	private Date crtDt;
	@Column(createTime = true, updateTime = true)
	@ColumnTypeHandler(DynamicTimestampTypeHandler.class)
	private Date uptDt;

	public static enum OrgType implements DbEnum<String> {
		ORG("org"),
		DEPARTMENT("department"),
		;


		private final String value;

		OrgType(String value) {
			this.value = value;
		}

		@Nonnull
		@Override
		public String getCode() {
			return value;
		}
	}
}

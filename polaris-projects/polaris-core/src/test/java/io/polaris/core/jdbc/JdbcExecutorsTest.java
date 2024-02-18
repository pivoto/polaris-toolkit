package io.polaris.core.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import io.polaris.core.jdbc.base.annotation.Key;
import io.polaris.core.jdbc.executor.JdbcExecutors;
import io.polaris.core.jdbc.sql.annotation.EntitySelect;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.junit.jupiter.api.Test;

public class JdbcExecutorsTest {


	@Test
	void test02() throws SQLException {
		TestInterface testInterface = Jdbcs.createExecutor(TestInterface.class);
		try (Connection conn = Jdbcs.getConnection("jdbc:oracle:thin:@localhost:1521/cmisdb", "basesv", "basesv");) {
			JdbcExecutors.setCurrentConnection(conn);

			System.out.println(testInterface.getTenantList(TenantEntity.builder().tenantId("C421").build()));

		} finally {
			JdbcExecutors.clearCurrentConnection();
		}
	}

	public interface TestInterface {

		@EntitySelect(table = TenantEntity.class, byId = false)
		List<TenantEntity> getTenantList(@Key(BindingKeys.ENTITY) TenantEntity param);

		default String test2() {
			return "test";
		}
	}


	@Data
	@Table("BRM_TENANT")
	@FieldNameConstants
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class TenantEntity {
		@Id
		private String tenantId;
		private String tenantName;
		private String tenantSts;
		private String userLimited;
		private String mainUrl;
		private Date registerTime;
		private String intro;
		private String tenantIcon;
		private Long crtUser;
		private Long uptUser;
		private Date crtDt;
		private Date uptDt;
	}
}

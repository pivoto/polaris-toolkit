package io.polaris.core.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import io.polaris.core.TestConsole;
import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import io.polaris.core.jdbc.base.annotation.Key;
import io.polaris.core.jdbc.executor.JdbcExecutors;
import io.polaris.core.jdbc.sql.annotation.EntityDelete;
import io.polaris.core.jdbc.sql.annotation.EntityInsert;
import io.polaris.core.jdbc.sql.annotation.EntitySelect;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.random.Randoms;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.junit.jupiter.api.Test;

public class JdbcExecutorsTest {


	@Test
	void test02() throws SQLException {
		TestInterface testInterface = Jdbcs.createExecutor(TestInterface.class);
		try (Connection conn = Jdbcs.getConnection("jdbc:oracle:thin:@localhost:1521/cmisdb", "cmis_config", "cmis_config");) {
			JdbcExecutors.setCurrentConnection(conn);
			conn.setAutoCommit(false);

			String sys = Randoms.randomString(10);

			JobEnv entity = JobEnv.builder().id(sys).profile("test").sysId(sys).build();

			TestConsole.println("insert: {}", testInterface.insert(conn, entity));
			conn.commit();

			TestConsole.println("select: {}",testInterface.getList(conn, JobEnv.builder().build()));

			TestConsole.println("delete: {}", testInterface.delete(conn, entity));
			conn.commit();
		} finally {
			JdbcExecutors.clearCurrentConnection();
		}
	}

	public interface TestInterface {

		@EntitySelect(table = JobEnv.class, byId = false)
		List<JobEnv> getList(Connection conn, @Key(BindingKeys.ENTITY) JobEnv param);

		@EntityInsert(table = JobEnv.class)
		int insert(Connection conn, @Key(BindingKeys.ENTITY) JobEnv param);

		@EntityDelete(table = JobEnv.class, byId = true)
		int delete(Connection conn, @Key(BindingKeys.ENTITY) JobEnv param);


		default String test2() {
			return "test";
		}
	}


	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Table(value = "CRM_JOB_ENV")
	@FieldNameConstants
	public static class JobEnv implements Serializable {
		private static final long serialVersionUID = 1L;
		@Id
		@Column(value = "ID")
		private String id;
		@Column(value = "PROFILE")
		private String profile;
		@Column(value = "SYS_ID")
		private String sysId;
		@Column(value = "ZK_ADDRESS")
		private String zkAddress;
		@Column(value = "ZK_REG_PATH")
		private String zkRegPath;
		@Column(value = "ZK_JOB_NAMESPACE")
		private String zkJobNamespace;
		@Column(value = "BASE_SLEEP_TIME")
		private Integer baseSleepTime = 1000;
		@Column(value = "MAX_SLEEP_TIME")
		private Integer maxSleepTime = 3000;
		@Column(value = "MAX_RETRIES")
		private Integer maxRetries = 3;
		@Column(value = "SESSION_TIMEOUT")
		private Integer sessionTimeout;
		@Column(value = "CONNECTION_TIMEOUT")
		private Integer connectionTimeout;
		@Column(value = "DIGEST")
		private String digest;
		@Column(value = "CTX_ATTRS")
		private String ctxAttrs;
		@Column(value = "DELETED", logicDeleted = true)
		private Boolean deleted;
		@Column(value = "CRT_USER", updatable = false)
		private Long crtUser;
		@Column(value = "CRT_USER_NAME", updatable = false)
		private String crtUserName;
		@Column(value = "CRT_DT", updatable = false, createTime = true)
		private Date crtDt;
		@Column(value = "UPT_USER")
		private Long uptUser;
		@Column(value = "UPT_USER_NAME")
		private String uptUserName;
		@Column(value = "UPT_DT", updateTime = true)
		private Date uptDt;

	}

}

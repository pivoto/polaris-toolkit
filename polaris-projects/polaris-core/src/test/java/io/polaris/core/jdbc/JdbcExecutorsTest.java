package io.polaris.core.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import io.polaris.core.jdbc.base.annotation.Key;
import io.polaris.core.jdbc.base.annotation.SqlQuery;
import io.polaris.core.jdbc.executor.JdbcExecutors;
import io.polaris.core.jdbc.sql.annotation.EntityInsert;
import io.polaris.core.jdbc.sql.annotation.EntitySelect;
import io.polaris.core.jdbc.sql.annotation.SqlEntityDeclared;
import io.polaris.core.jdbc.sql.annotation.SqlRaw;
import io.polaris.core.jdbc.sql.annotation.SqlRawSimple;
import io.polaris.core.jdbc.sql.annotation.segment.SqlRawItem;
import io.polaris.core.jdbc.sql.annotation.segment.SqlRawItem1;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.log.ILoggers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.junit.jupiter.api.Test;

public class JdbcExecutorsTest {

	@Test
	void testCreateTable() throws SQLException {
		try (Connection conn = Jdbcs.getConnection("jdbc:oracle:thin:@localhost:1521/cmisdb", "basesv", "basesv");) {
			Jdbcs.update(conn, "" +
				"create table demo_test01(" +
				"id number generated always as identity, " +
				"name varchar2(30)" +
				")" +
				"");
		}
	}

	@Test
	void testDropTable() throws SQLException {
		try (Connection conn = Jdbcs.getConnection("jdbc:oracle:thin:@localhost:1521/cmisdb", "basesv", "basesv");) {
			Jdbcs.update(conn, "drop table demo_test01");
		}
	}

	@Test
	void test02() throws SQLException {
		TestInterface testInterface = Jdbcs.createExecutor(TestInterface.class);
		try (Connection conn = Jdbcs.getConnection("jdbc:oracle:thin:@localhost:1521/cmisdb", "basesv", "basesv");) {
			JdbcExecutors.setCurrentConnection(conn);

			System.out.println(testInterface.getTenantList(TenantEntity.builder().tenantId("C421").build()));

			testInterface.insert(DemoTest01Entity.builder().name("test").build());
			testInterface.insert(DemoTest01Entity.builder().name("test").build());

			System.out.println(testInterface.getList(DemoTest01Entity.builder().name("test").build()));

		} finally {
			JdbcExecutors.clearCurrentConnection();
		}
	}

	@Test
	void test03() throws SQLException {
		TestInterface testInterface = Jdbcs.createExecutor(TestInterface.class);
		try (Connection conn = Jdbcs.getConnection("jdbc:oracle:thin:@localhost:1521/cmisdb", "basesv", "basesv");) {
			JdbcExecutors.setCurrentConnection(conn);

			ILoggers.of(getClass()).info("entity: {}", testInterface.getByIds(new Long[]{1L, 2L, 3L}));
			ILoggers.of(getClass()).info("entity: {}", testInterface.getByIds2(new Long[]{1L, 2L, 3L}));
			ILoggers.of(getClass()).info("entity: {}", testInterface.getByIds3(new Long[]{1L, 2L, 3L}));

		} finally {
			JdbcExecutors.clearCurrentConnection();
		}
	}

	public interface TestInterface {

		@EntityInsert(table = DemoTest01Entity.class)
		int insert(@Key(BindingKeys.ENTITY) DemoTest01Entity param);

		@SqlQuery
		@SqlEntityDeclared(table = {DemoTest01Entity.class}, alias = {"x"})
		@SqlRaw({
			@SqlRawItem(
				forEachKey = "ids", itemKey = "id", separator = " union all "
				, value = "select &{x.*} from &{x} where &{x.id} = #{id}"
			)
		})
		List<DemoTest01Entity> getByIds(@Key("ids") Long[] ids);

		@SqlQuery
		@SqlEntityDeclared(table = {DemoTest01Entity.class}, alias = {"x"})
		@SqlRaw({
			@SqlRawItem("select &{x.*} from &{x} where 1=1"),
			@SqlRawItem(forEachKey = "ids", itemKey = "id", separator = ",",open=" and &{x.id} in (",close=") ",
				value="#{id}"
			),
			@SqlRawItem(""),
		})
		List<DemoTest01Entity> getByIds2(@Key("ids") Long[] ids);
		@SqlQuery
		@SqlEntityDeclared(table = {DemoTest01Entity.class}, alias = {"x"})
		@SqlRaw({
			@SqlRawItem("select &{x.*} from &{x} where 1=1"),
			@SqlRawItem(forEachKey = "ids", itemKey = "id", separator = " or ",open=" and ( ",close=" ) ",
				subset = {
					@SqlRawItem1("&{x.id}"),
					@SqlRawItem1("="),
					@SqlRawItem1("#{id}"),
				}
			)
		})
		List<DemoTest01Entity> getByIds3(@Key("ids") Long[] ids);

		@SqlQuery
		@SqlEntityDeclared(table = {DemoTest01Entity.class}, alias = {"x"})
		@SqlRawSimple("select &{x.*} from &{x} where &{x.id} = #{id}")
//		@EntitySelect(table = DemoTest01Entity.class, byId = true)
		DemoTest01Entity getById(@Key(DemoTest01Entity.Fields.id) Long id);

		@EntitySelect(table = DemoTest01Entity.class, byId = false)
		List<DemoTest01Entity> getList(@Key(BindingKeys.ENTITY) DemoTest01Entity param);

		@EntitySelect(table = TenantEntity.class, byId = false)
		List<TenantEntity> getTenantList(@Key(BindingKeys.ENTITY) TenantEntity param);

		default String test2() {
			return "test";
		}
	}

	@Data
	@Table("DEMO_TEST01")
	@FieldNameConstants
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class DemoTest01Entity {
		@Id
		private Long id;
		private String name;
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

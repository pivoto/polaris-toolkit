package io.polaris.core.jdbc.executor;

import java.sql.SQLException;

import io.polaris.core.jdbc.Jdbcs;
import io.polaris.core.jdbc.entity.DemoTest01Entity;
import io.polaris.core.jdbc.entity.DemoTest02Entity;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author Qt
 * @since  Feb 08, 2024
 */
public class OracleJdbcExecutorTest extends BaseOracleJdbcTest {
	private static final Logger log = Loggers.of();
	DemoTestExecutor demoTestExecutor = Jdbcs.createExecutor(DemoTestExecutor.class);

	@BeforeAll
	static void beforeAll(TestInfo testInfo) {
//		tableCreate();
//		tableInsert();
	}

	@AfterAll
	static void afterAll() {
//			tableDrop();
	}

	@Test
	void test01(TestInfo testInfo) {
		doWithConnection(conn -> {
			log.info("[insert] result: {}", toString(
				demoTestExecutor.insertDemoTest02(DemoTest02Entity.builder().name("test").build())
			));

			log.info("[select] result: {}", toString(
				demoTestExecutor.getDemoTest02List(DemoTest02Entity.builder().name("test%").build())
			));
			log.info("[delete] result: {}", toString(
				demoTestExecutor.deleteDemoTest02(DemoTest02Entity.builder().name("test").build())
			));
			log.info("[select] result: {}", toString(
				demoTestExecutor.getDemoTest02List(DemoTest02Entity.builder().name("test%").build())
			));
		});
	}

	@Test
	void test02(TestInfo testInfo) throws SQLException {
		doWithConnection(conn -> {

			log.info("[insert] result: {}", toString(
				demoTestExecutor.insertDemoTest01(DemoTest01Entity.builder().name("test").build())
			));
			log.info("[insert] result: {}", toString(
				demoTestExecutor.insertDemoTest01(DemoTest01Entity.builder().name("test").build())
			));

			log.info("[select] result: {}", toString(
				demoTestExecutor.getDemoTest01List(DemoTest01Entity.builder().name("test%").build())
			));
			log.info("[delete] result: {}", toString(
				demoTestExecutor.deleteDemoTest01(DemoTest01Entity.builder().name("test").build())
			));
			log.info("[select] result: {}", toString(
				demoTestExecutor.getDemoTest01List(DemoTest01Entity.builder().name("test%").build())
			));

		});
	}

	@Test
	void test03(TestInfo testInfo) throws SQLException {
		doWithConnection(conn -> {

			log.info("[select] getDemoTest01ListByIds1: {}", toString(
				demoTestExecutor.getDemoTest01ListByIds1(new Long[]{1L, 2L, 3L})
			));
			log.info("[select] getDemoTest01ListByIds2: {}", toString(
				demoTestExecutor.getDemoTest01ListByIds2(new Long[]{1L, 2L, 3L})
			));
			log.info("[select] getDemoTest01ListByIds3: {}", toString(
				demoTestExecutor.getDemoTest01ListByIds3(new Long[]{1L, 2L, 3L})
			));

		});
	}
}

package io.polaris.core.jdbc.executor;

import java.sql.SQLException;

import io.polaris.core.collection.Iterables;
import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.jdbc.entity.DemoTest01Entity;
import io.polaris.core.jdbc.entity.DemoTest02Entity;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class JdbcRecordersTest {
	private static final Logger log = Loggers.of();
	DemoTestExecutor demoTestExecutor = JdbcRecorders.of(DemoTestExecutor.class);


	@Test
	void test01(TestInfo testInfo) {
		demoTestExecutor.insertDemoTest01(DemoTest01Entity.builder().name("test").build());
		log.info("[insert] {}", JdbcRecorders.pollALlSqlText());
		demoTestExecutor.insertDemoTest01(DemoTest01Entity.builder().name("test").build());
		log.info("[insert] {}", JdbcRecorders.pollALlSqlText());
		demoTestExecutor.getDemoTest01List(DemoTest01Entity.builder().name("test%").build());
		log.info("[select] {}", JdbcRecorders.pollALlSqlText());
		demoTestExecutor.deleteDemoTest01(DemoTest01Entity.builder().name("test").build());
		log.info("[delete] {}", JdbcRecorders.pollALlSqlText());
		demoTestExecutor.getDemoTest01List(DemoTest01Entity.builder().name("test%").build());
		log.info("[select] {}", JdbcRecorders.pollALlSqlText());
	}

	@Test
	void test02(TestInfo testInfo) {
		demoTestExecutor.insertDemoTest02(DemoTest02Entity.builder().name("test").build());
		log.info("[insert] {}", JdbcRecorders.pollALlSqlText());
		demoTestExecutor.getDemoTest02List(DemoTest02Entity.builder().name("test%").build());
		log.info("[select] {}", JdbcRecorders.pollALlSqlText());
		demoTestExecutor.deleteDemoTest02(DemoTest02Entity.builder().name("test").build());
		log.info("[delete] {}", JdbcRecorders.pollALlSqlText());
		demoTestExecutor.getDemoTest02List(DemoTest02Entity.builder().name("test%").build());
		log.info("[select] {}", JdbcRecorders.pollALlSqlText());
	}

	@Test
	void test03(TestInfo testInfo) throws SQLException {
		demoTestExecutor.getDemoTest01ListByIds1(new Long[]{1L, 2L, 3L});
		log.info("[select] {}", JdbcRecorders.pollALlSqlText());
		demoTestExecutor.getDemoTest01ListByIds2(new Long[]{1L, 2L, 3L});
		log.info("[select] {}", JdbcRecorders.pollALlSqlText());
		demoTestExecutor.getDemoTest01ListByIds3(new Long[]{1L, 2L, 3L});
		log.info("[select] {}", JdbcRecorders.pollALlSqlText());
	}

}

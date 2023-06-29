package io.polaris.dbv;

import com.alibaba.fastjson2.JSON;
import io.polaris.dbv.toolkit.DBKit;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.rules.TestName;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Qt
 * @version Oct 08, 2019
 */
@Slf4j
public class DbvBaseTest {
	protected static Connection conn;
	@ClassRule
	public static ClassRuleTestName testName = new ClassRuleTestName();
	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void init() throws SQLException {
		try {
			// 执行运行类的static 块方法
			Class<?> testClass = testName.getDescription().getTestClass();
			log.info("init testClass : {}", testClass.getName());
			testClass.newInstance();
		} catch (Exception e) {
		}

		log.info("get database connection");
		conn = Dbv.getConnection("test.xml");
	}

	@AfterClass
	public static void destroy() {
		log.info("close database connection");
		DBKit.close(conn);
	}

	@Before
	public void before() {
		log.info("-----------------------------------------------------------------------------------");
		log.info("Before Test: {}", name.getMethodName());
	}

	@After
	public void after() {
		log.info("After Test: {}", name.getMethodName());
		log.info("-----------------------------------------------------------------------------------");
	}

	protected void toString(Collection<?> collection) {
		for (Object o : collection) {
			log.info("{}", o);
		}
	}

	protected void show(Collection<?> collection) {
		for (Object o : collection) {
			log.info("{}", JSON.toJSONString(o));
		}
	}
}

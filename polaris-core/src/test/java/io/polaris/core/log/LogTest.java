package io.polaris.core.log;

import io.polaris.core.io.Consoles;
import io.polaris.core.log.support.DynamicLoggerResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Aug 04, 2023
 */
public class LogTest {
	@BeforeAll
	public static void beforeAll() {
		System.setProperty(DynamicLoggerResolver.PREFER_DYNAMIC_SLF4J, "true");
	}

	@Test
	void test01() {
		ILogger log = Loggers.of(getClass());
		Consoles.log("", log);
		log.trace("test....");
		log.trace("test....{}", "arg1");
		log.trace(new Throwable(), "test....{}", "arg1");
		log.debug("test....");
		log.debug("test....{}", "arg1");
		log.debug(new Throwable(), "test....{}", "arg1");
		log.info("test....");
		log.info("test....{}", "arg1");
		log.info(new Throwable(), "test....{}", "arg1");
		log.warn("test....");
		log.warn("test....{}", "arg1");
		log.warn(new Throwable(), "test....{}", "arg1");
		log.error("test....");
		log.error("test....{}", "arg1");
		log.error(new Throwable(), "test....{}", "arg1");
	}
}

package io.polaris.core.log;

import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since 1.8,  Aug 04, 2023
 */
public class LogTest {
	@Test
	void test01() {
		ILogger log = ILoggers.of(getClass());
		log.trace("test....");
		log.debug("test....");
		log.info("test....");
		log.warn("test....");
		log.error("test....");
	}
}

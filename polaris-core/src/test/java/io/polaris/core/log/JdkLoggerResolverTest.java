package io.polaris.core.log;

import io.polaris.core.log.support.JdkLoggerResolver;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Jan 06, 2025
 */
public class JdkLoggerResolverTest {
	@Test
	void test01() throws ClassNotFoundException {
		Loggers.setResolver(new JdkLoggerResolver());
		Loggers.of(JdkLoggerResolverTest.class).error("test..");
	}
}

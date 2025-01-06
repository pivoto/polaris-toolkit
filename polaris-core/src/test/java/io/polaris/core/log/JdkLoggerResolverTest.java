package io.polaris.core.log;

import io.polaris.core.classloader.ClassLoaders;
import io.polaris.core.log.support.JdkLoggerResolver;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Jan 06, 2025
 */
public class JdkLoggerResolverTest {
	@Test
	void test01() throws ClassNotFoundException {
		ILoggers.setResolver(new JdkLoggerResolver());
		ILoggers.of(JdkLoggerResolverTest.class).error("test..");
	}
}

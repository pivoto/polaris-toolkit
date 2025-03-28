package io.polaris.core.log.support;

import io.polaris.core.log.ILogResolver;
import io.polaris.core.log.ILogger;

/**
 * @author Qt
 */
public class JdkLoggerResolver implements ILogResolver {
	@Override
	public ILogger getLogger(String name) {
		return new JdkLogger(name);
	}
}

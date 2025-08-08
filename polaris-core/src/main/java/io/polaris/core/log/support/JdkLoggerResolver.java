package io.polaris.core.log.support;

import io.polaris.core.log.ILogResolver;
import io.polaris.core.log.Logger;

/**
 * @author Qt
 */
public class JdkLoggerResolver implements ILogResolver {
	@Override
	public Logger getLogger(String name) {
		return new JdkLogger(name);
	}
}

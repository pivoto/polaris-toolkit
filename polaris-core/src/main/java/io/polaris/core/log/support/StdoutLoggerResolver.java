package io.polaris.core.log.support;

import io.polaris.core.log.LogResolver;
import io.polaris.core.log.Logger;

/**
 * @author Qt
 */
public class StdoutLoggerResolver implements LogResolver {

	@Override
	public Logger getLogger(String name) {
		return new StdoutLogger(name);
	}

}

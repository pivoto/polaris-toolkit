package io.polaris.core.service.impl;

import io.polaris.core.log.ILogger;
import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceDefault;
import io.polaris.core.service.ServiceProperty;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceDefault()
@ServiceProperty(name = "key", value = "test0")
public class Test0Service implements ITestService {
	private static final ILogger log = ILogger.of(ITestService.class);

	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

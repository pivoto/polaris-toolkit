package io.polaris.core.service.impl;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceProperty;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceProperty(name = "key", value = "test2")
public class Test2Service implements ITestService {
	private static final ILogger log = Loggers.of(ITestService.class);
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

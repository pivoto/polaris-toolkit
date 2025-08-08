package io.polaris.core.service.impl;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceOrder;
import io.polaris.core.service.ServiceProperty;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceOrder(50)
@ServiceProperty(name = "key", value = "test5")
public class Test5Service implements ITestService {
	private static final ILogger log = Loggers.of(Test5Service.class);
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

package io.polaris.core.service.impl;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceOrder;
import io.polaris.core.service.ServiceProperty;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceOrder(100)
@ServiceProperty(name = "key", value = "test4")
public class Test4Service implements ITestService {
	private static final ILogger log = ILoggers.of(Test4Service.class);
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

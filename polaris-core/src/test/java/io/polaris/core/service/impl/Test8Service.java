package io.polaris.core.service.impl;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceDefault;
import io.polaris.core.service.ServiceProperty;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceDefault(-100)
@ServiceProperty(name = "key", value = "test8")
public class Test8Service implements ITestService {
	private static final ILogger log = ILoggers.of(Test8Service.class);

	private ITestService service;

	public Test8Service(final ITestService service) {
		this.service = service;
	}

	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
		service.call();
	}
}

package io.polaris.core.service.impl;

import io.polaris.core.log.ILogger;
import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceDefault;
import io.polaris.core.service.ServiceProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceDefault(-100)
@ServiceProperty(name = "key", value = "test9")
public class Test9Service implements ITestService {
	private static final ILogger log = ILogger.of(Test9Service.class);

	private ITestService service;

	public Test9Service(final ITestService service) {
		this.service = service;
	}

	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
		service.call();
	}
}

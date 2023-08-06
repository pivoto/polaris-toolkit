package io.polaris.core.service.impl;

import io.polaris.core.log.ILogger;
import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceProperty(name = "key", value = "test3")
public class Test3Service implements ITestService {
	private static final ILogger log = ILogger.of(ITestService.class);
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

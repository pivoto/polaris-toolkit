package io.polaris.core.service.impl;

import io.polaris.core.log.ILogger;
import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceDefault;
import io.polaris.core.service.ServiceName;
import io.polaris.core.service.ServiceProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceDefault(-100)
@ServiceName("test")
@ServiceProperty(name = "key", value = "test7")
public class Test7Service implements ITestService {
	private static final ILogger log = ILogger.of(Test7Service.class);
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

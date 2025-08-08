package io.polaris.core.service.impl;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceName;
import io.polaris.core.service.ServiceOrder;
import io.polaris.core.service.ServiceProperty;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceOrder(50)
@ServiceName("test")
@ServiceProperty(name = "key", value = "test6")
public class Test6Service implements ITestService {
	private static final Logger log = Loggers.of(Test6Service.class);

	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

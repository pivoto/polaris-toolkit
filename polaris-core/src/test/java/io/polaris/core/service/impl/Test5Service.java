package io.polaris.core.service.impl;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.SpiTestService;
import io.polaris.core.service.SpiOrder;
import io.polaris.core.service.ServiceProperty;

/**
 * @author Qt
 */
@SpiOrder(50)
@ServiceProperty(name = "key", value = "test5")
public class Test5Service implements SpiTestService {
	private static final Logger log = Loggers.of(Test5Service.class);
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

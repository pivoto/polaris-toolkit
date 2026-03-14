package io.polaris.core.service.impl;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.SpiTestService;
import io.polaris.core.service.SpiName;
import io.polaris.core.service.SpiOrder;
import io.polaris.core.service.SpiProperty;

/**
 * @author Qt
 */
@SpiOrder(50)
@SpiName("test")
@SpiProperty(name = "key", value = "test6")
public class Test6Service implements SpiTestService {
	private static final Logger log = Loggers.of(Test6Service.class);

	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

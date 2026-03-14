package io.polaris.core.service.impl;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.SpiTestService;
import io.polaris.core.service.SpiOrder;
import io.polaris.core.service.SpiProperty;

/**
 * @author Qt
 */
@SpiOrder(100)
@SpiProperty(name = "key", value = "test4")
public class Test4Service implements SpiTestService {
	private static final Logger log = Loggers.of(Test4Service.class);
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

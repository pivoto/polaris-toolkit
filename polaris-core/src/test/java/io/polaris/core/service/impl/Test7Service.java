package io.polaris.core.service.impl;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.spi.SpiDefault;
import io.polaris.core.service.SpiTestService;
import io.polaris.core.spi.SpiName;
import io.polaris.core.spi.SpiProperty;

/**
 * @author Qt
 */
@SpiDefault(-100)
@SpiName("test")
@SpiProperty(name = "key", value = "test7")
public class Test7Service implements SpiTestService {
	private static final Logger log = Loggers.of(Test7Service.class);
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

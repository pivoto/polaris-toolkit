package io.polaris.core.spi.impl;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.spi.SpiTestService;
import io.polaris.core.spi.SpiProperty;

/**
 * @author Qt
 */
@SpiProperty(name = "key", value = "test2")
public class Test2Service implements SpiTestService {
	private static final Logger log = Loggers.of(SpiTestService.class);
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

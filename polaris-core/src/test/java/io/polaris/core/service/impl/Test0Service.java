package io.polaris.core.service.impl;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.SpiProperty;
import io.polaris.core.service.SpiDefault;
import io.polaris.core.service.SpiTestService;

/**
 * @author Qt
 */
@SpiDefault()
@SpiProperty(name = "key", value = "test0")
public class Test0Service implements SpiTestService {
	private static final Logger log = Loggers.of(SpiTestService.class);

	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

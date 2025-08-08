package io.polaris.core.service.impl;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.SpiTestService;
import io.polaris.core.service.ServiceProperty;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceProperty(name = "key", value = "test1")
public class Test1Service implements SpiTestService {
	private static final Logger log = Loggers.of(SpiTestService.class);
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

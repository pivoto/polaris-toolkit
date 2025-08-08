package io.polaris.core.service.impl;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.SpiTestService;
import io.polaris.core.service.ServiceDefault;
import io.polaris.core.service.ServiceProperty;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceDefault(-100)
@ServiceProperty(name = "key", value = "test9")
public class Test9Service implements SpiTestService {
	private static final Logger log = Loggers.of(Test9Service.class);

	private SpiTestService service;

	public Test9Service(final SpiTestService service) {
		this.service = service;
	}

	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
		service.call();
	}
}

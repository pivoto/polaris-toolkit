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
@ServiceProperty(name = "key", value = "test8")
public class Test8Service implements SpiTestService {
	private static final Logger log = Loggers.of(Test8Service.class);

	private SpiTestService service;

	public Test8Service(final SpiTestService service) {
		this.service = service;
	}

	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
		service.call();
	}
}

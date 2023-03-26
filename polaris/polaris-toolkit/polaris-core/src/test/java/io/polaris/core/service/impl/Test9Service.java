package io.polaris.core.service.impl;

import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceDefault;
import io.polaris.core.service.ServiceProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Qt
 * @since 1.8
 */
@Slf4j
@ServiceDefault(-100)
@ServiceProperty(name = "key", value = "test9")
public class Test9Service implements ITestService {

	private ITestService service;

	public Test9Service(final ITestService service) {
		this.service = service;
	}

	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
		service.call();
	}
}

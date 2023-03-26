package io.polaris.core.service.impl;

import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Qt
 * @since 1.8
 */
@Slf4j
@ServiceProperty(name = "key", value = "test2")
public class Test2Service implements ITestService {
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

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
@ServiceDefault()
@ServiceProperty(name = "key", value = "test0")
public class Test0Service implements ITestService {
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

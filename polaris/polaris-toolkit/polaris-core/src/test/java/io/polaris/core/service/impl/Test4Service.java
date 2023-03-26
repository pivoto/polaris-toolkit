package io.polaris.core.service.impl;

import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceOrder;
import io.polaris.core.service.ServiceProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Qt
 * @since 1.8
 */
@Slf4j
@ServiceOrder(100)
@ServiceProperty(name = "key", value = "test4")
public class Test4Service implements ITestService {
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

package io.polaris.core.service.impl;

import io.polaris.core.service.ITestService;
import io.polaris.core.service.ServiceName;
import io.polaris.core.service.ServiceOrder;
import io.polaris.core.service.ServiceProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Qt
 * @since 1.8
 */
@Slf4j
@ServiceOrder(50)
@ServiceName("test")
@ServiceProperty(name = "key", value = "test6")
public class Test6Service implements ITestService {
	@Override
	public void call() {
		log.info("called in {}", this.getClass().getName());
	}
}

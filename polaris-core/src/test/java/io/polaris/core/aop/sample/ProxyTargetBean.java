package io.polaris.core.aop.sample;

import java.util.function.Supplier;

import io.polaris.core.io.Consoles;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.random.Randoms;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class ProxyTargetBean {
	static Logger log = Loggers.of(ProxyTargetBean.class);

	public void test(int a, int b) {
		log.info("test... a={},b={}", a, b);
	}

	public Object get() {
		log.info("get...");
		return Randoms.randomString(10);
	}

	public void accept(Object arg) {
		log.info("accept... {}", arg);
	}
}

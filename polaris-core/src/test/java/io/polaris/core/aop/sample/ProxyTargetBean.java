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
public class ProxyTargetBean implements Testable, Raiseable {
	static Logger log = Loggers.of(ProxyTargetBean.class);

	@Override
	public void test(Object... args) {
		log.info("test... {}", args);
	}

	@Override
	public void raise(Object... args) throws UnsupportedOperationException, IllegalArgumentException, IllegalStateException {
		log.info("raise... {}", args);
		throw new UnsupportedOperationException();
	}

	Object get() throws Throwable {
		log.info("get...");
		return Randoms.randomString(10);
	}

	public void accept(Object arg) {
		log.info("accept... {}", arg);
	}
}

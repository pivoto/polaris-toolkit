package io.polaris.core.service;

import io.polaris.core.io.Consoles;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class ServiceLoaderTest {
	private static final ILogger log = ILoggers.of(ServiceLoaderTest.class);

	@Test
	void test01() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);

		Object[] args2 = new Object[]{loader.get()};
		Consoles.println(args2);
		Object[] args1 = new Object[]{loader.get("test")};
		Consoles.println(args1);

		for (Service<ITestService> service : loader.getProviders()) {
			Consoles.println(service);
		}
		Object[] args = new Object[]{loader.getNamings()};
		Consoles.println(args);
	}

	@Test
	void test02() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		Assertions.assertNotNull(loader.getSingleton());
		Object[] args = new Object[]{loader.getSingleton()};
		Consoles.println(args);
		loader.getSingleton().call();
	}

	@Test
	void test03() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		Assertions.assertNotNull(loader.getSingleton("test"));
		Object[] args = new Object[]{loader.getSingleton("test")};
		Consoles.println(args);
		loader.getSingleton("test").call();
	}

	@Test
	void test04() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		Assertions.assertNotNull(loader.getSingleton("key", "test0"));
		Object[] args = new Object[]{loader.getSingleton("key", "test0")};
		Consoles.println(args);
		loader.getSingleton("key", "test0").call();
	}

	@Test
	void test05() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		Assertions.assertNull(loader.getSingleton("key", "test"));
		Object[] args = new Object[]{loader.getSingleton("key", "test")};
		Consoles.println(args);
		Optional.ofNullable(loader.getSingleton("key", "test")).ifPresent(ITestService::call);
	}

}

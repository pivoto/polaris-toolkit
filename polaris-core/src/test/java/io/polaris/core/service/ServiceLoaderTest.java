package io.polaris.core.service;

import io.polaris.core.TestConsole;
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

		TestConsole.println(loader.get());
		TestConsole.println(loader.get("test"));

		for (Service<ITestService> service : loader.getProviders()) {
			TestConsole.println(service);
		}
		TestConsole.println(loader.getNamings());
	}

	@Test
	void test02() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		Assertions.assertNotNull(loader.getSingleton());
		TestConsole.println(loader.getSingleton());
		loader.getSingleton().call();
	}

	@Test
	void test03() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		Assertions.assertNotNull(loader.getSingleton("test"));
		TestConsole.println(loader.getSingleton("test"));
		loader.getSingleton("test").call();
	}

	@Test
	void test04() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		Assertions.assertNotNull(loader.getSingleton("key", "test0"));
		TestConsole.println(loader.getSingleton("key", "test0"));
		loader.getSingleton("key", "test0").call();
	}

	@Test
	void test05() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		Assertions.assertNull(loader.getSingleton("key", "test"));
		TestConsole.println(loader.getSingleton("key", "test"));
		Optional.ofNullable(loader.getSingleton("key", "test")).ifPresent(ITestService::call);
	}

}

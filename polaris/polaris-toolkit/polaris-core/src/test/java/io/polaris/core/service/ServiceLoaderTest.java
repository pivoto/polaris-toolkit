package io.polaris.core.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ServiceLoaderTest {

	@Test
	void test01() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		System.out.println(loader.get());
		System.out.println(loader.get("test"));

		for (Service<ITestService> service : loader.getProviders()) {
			System.out.println(service);
		}
		System.out.println(loader.getNamings());
	}

	@Test
	void test02() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		System.out.println(loader.getSingleton());
		System.out.println(loader.getSingleton());
		loader.getSingleton().call();
	}

	@Test
	void test03() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		System.out.println(loader.getSingleton("test"));
		System.out.println(loader.getSingleton("test"));
		loader.getSingleton("test").call();
	}

	@Test
	void test04() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		System.out.println(loader.getSingleton("key", "test0"));
		System.out.println(loader.getSingleton("key", "test0"));
		loader.getSingleton("key", "test0").call();
	}

	@Test
	void test05() {
		ServiceLoader<ITestService> loader = new ServiceLoader<>(ITestService.class);
		System.out.println(loader.getSingleton("key", "test"));
		System.out.println(loader.getSingleton("key", "test"));
		Optional.ofNullable(loader.getSingleton("key", "test")).ifPresent(ITestService::call);
	}

}

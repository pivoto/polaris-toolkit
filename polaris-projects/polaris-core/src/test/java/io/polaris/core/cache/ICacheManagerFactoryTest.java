package io.polaris.core.cache;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ICacheManagerFactoryTest {
	@BeforeAll
	static void beforeAll() {
		System.setProperty(MapCacheManager.KEY_MAX_CAPACITY, "10");
	}

	@Test
	void test01() {
		ICacheManagerFactory factory = ICacheManagerFactory.defaultCacheManagerFactory();
		Assertions.assertInstanceOf(MapCacheManagerFactory.class, factory);
		Assertions.assertSame(factory.getCacheManager(), ICacheManagerFactory.defaultCacheManager());

		ICache<String, Object> cache = ICacheManagerFactory.defaultCacheManager().getCache("test");
		Assertions.assertInstanceOf(MapCache.class, cache);

		for (int i = 0; i < 20; i++) {
			cache.put("key-" + i, "val-" + i);
		}

		for (int i = 0; i < 10; i++) {
			TestConsole.printx("{} -> {}", "key-" + i, cache.get("key-" + i));
			Assertions.assertNull(cache.get("key-" + i));
		}

		for (int i = 10; i < 20; i++) {
			TestConsole.printx("{} -> {}", "key-" + i, cache.get("key-" + i));
			Assertions.assertEquals("val-" + i, cache.getIfPresent("key-" + i));
		}

	}
}


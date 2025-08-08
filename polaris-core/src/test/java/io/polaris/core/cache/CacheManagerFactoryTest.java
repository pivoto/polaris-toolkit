package io.polaris.core.cache;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CacheManagerFactoryTest {
	@BeforeAll
	static void beforeAll() {
		System.setProperty(MapCacheManager.KEY_MAX_CAPACITY, "10");
	}

	@Test
	void test01() {
		CacheManagerFactory factory = CacheManagerFactory.defaultCacheManagerFactory();
		Assertions.assertInstanceOf(MapCacheManagerFactory.class, factory);
		Assertions.assertSame(factory.getCacheManager(), CacheManagerFactory.defaultCacheManager());

		Cache<String, Object> cache = CacheManagerFactory.defaultCacheManager().getCache("test");
		Assertions.assertInstanceOf(MapCache.class, cache);

		for (int i = 0; i < 20; i++) {
			cache.put("key-" + i, "val-" + i);
		}

		for (int i = 0; i < 10; i++) {
			Object[] args = new Object[]{"key-" + i, cache.get("key-" + i)};
			Consoles.log("{} -> {}", args);
			Assertions.assertNull(cache.get("key-" + i));
		}

		for (int i = 10; i < 20; i++) {
			Object[] args = new Object[]{"key-" + i, cache.get("key-" + i)};
			Consoles.log("{} -> {}", args);
			Assertions.assertEquals("val-" + i, cache.getIfPresent("key-" + i));
		}

	}
}


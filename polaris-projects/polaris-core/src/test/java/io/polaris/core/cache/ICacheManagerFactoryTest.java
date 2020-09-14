package io.polaris.core.cache;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ICacheManagerFactoryTest {
	@BeforeAll
	static void beforeAll() {
		System.setProperty(MapCacheManager.KEY_MAX_CAPACITY, "10");
	}

	@Test
	void test01() {
		System.out.println(ICacheManagerFactory.defaultCacheManagerFactory());
		System.out.println(ICacheManagerFactory.defaultCacheManager());
		System.out.println(ICacheManagerFactory.defaultCacheManager());

		ICache<String, Object> cache = ICacheManagerFactory.defaultCacheManager().getCache("test");

		for (int i = 0; i < 20; i++) {
			cache.put("key-" + i, "val-" + i);
		}

		for (int i = 0; i < 20; i++) {
			System.out.printf("%s -> %s%n", "key-" + i, cache.get("key-" + i));
		}


	}
}


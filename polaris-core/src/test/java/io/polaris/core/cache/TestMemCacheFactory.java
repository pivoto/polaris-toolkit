package io.polaris.core.cache;

import io.polaris.core.io.Consoles;
import io.polaris.core.random.Randoms;
import io.polaris.core.tuple.Ref;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestMemCacheFactory {

	@Test
	void test01() {
		MemCache<Integer, Object> cache = MemCacheFactory.defaultBuild(10, true);
		Assertions.assertNotNull(cache);
		for (int i = 0; i < 100; i++) {
			cache.put(i, i);
			for (int j = 0; j < 100; j++) {
				cache.get(Randoms.randomInt(1 + i));
			}
		}
		for (int i = 0; i < 100; i++) {
			Ref<Object> ref = cache.get(i);
			if (ref != null) {
				Consoles.log(ref.get());
			}
		}
	}
}

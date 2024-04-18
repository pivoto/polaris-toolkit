package io.polaris.json;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.json.Jsons;
import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.Test;

class FastjsonsTest {


	@Test
	void test01() {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 5; i++) {
			map.put("" + i, Randoms.randomString(6));
		}

		System.out.printf("%s%n", Fastjsons.toJsonString(map));
	}

	@Test
	void test02() {
		System.out.printf("%s%n", Fastjsons.toJavaObject("{a:123}", Map.class));
	}


}

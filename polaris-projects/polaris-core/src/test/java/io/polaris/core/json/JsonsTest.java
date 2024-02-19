package io.polaris.core.json;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.TestConsole;
import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.Test;

class JsonsTest {

	@Test
	void test00() {
		TestConsole.println(Jsons.getJsonSerializer());
	}

	@Test
	void test01() {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 5; i++) {
			map.put("" + i, Randoms.randomString(6));
		}

		TestConsole.println(Jsons.serialize(map));
	}

	@Test
	void test02() {
		TestConsole.println(Jsons.deserialize("{a:123}", Map.class));
	}


}

package io.polaris.json;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.json.Jsons;
import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class JacksonsTest {

	@Test
	void test00() {
		System.out.printf("%s%n", Jsons.getJsonSerializer());
	}

	@Test
	void test01() {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 5; i++) {
			map.put("" + i, Randoms.randomString(6));
		}

		System.out.printf("%s%n", Jsons.serialize(map));
	}

	@Test
	void test02() {
		System.out.printf("%s%n", Jsons.deserialize("{a:123}", Map.class));
	}


}

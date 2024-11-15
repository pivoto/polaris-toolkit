package io.polaris.core.json;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.io.Consoles;
import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.Test;

class JsonsTest {

	@Test
	void test00() {
		Object[] args = new Object[]{Jsons.getJsonSerializer()};
		Consoles.println(args);
	}

	@Test
	void test01() {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 5; i++) {
			map.put("" + i, Randoms.randomString(6));
		}

		String msg = Jsons.serialize(map);
		Consoles.println(msg);
	}

	@Test
	void test02() {
		Object[] args = new Object[]{Jsons.deserialize("{a:123}", Map.class)};
		Consoles.println(args);
	}


}

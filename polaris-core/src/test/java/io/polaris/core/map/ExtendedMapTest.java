package io.polaris.core.map;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExtendedMapTest {

	@Test
	void test01() {
		Map<String, String> raw = new HashMap<>();
		ExtendedMap<String, String> map = new ExtendedMap<>(raw);
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		Consoles.println("raw: {}",  raw);
		Consoles.println("map: {}",  map);

		assertEquals("1", map.get("a"));
		assertEquals("2", map.get("b"));
		assertEquals("3", map.get("c"));
		assertNull(raw.get("a"));
		assertNull(map.get("d"));
	}
}

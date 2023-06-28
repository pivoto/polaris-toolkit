package io.polaris.core.map;

import org.junit.jupiter.api.Test;

import java.util.Map;

class MapsTest {

	@Test
	void testNewLimitCapacityMap() {
		Map<Object, Object> map = Maps.newLimitCapacityMap(5);
		for (int i = 0; i < 10; i++) {
			map.put("key" + i, "val" + i);
		}
		System.out.println(map);
	}

}

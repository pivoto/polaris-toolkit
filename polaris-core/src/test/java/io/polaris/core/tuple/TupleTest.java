package io.polaris.core.tuple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since  Sep 27, 2023
 */
public class TupleTest {


	@Test
	void test01() {
		Map<Tuple, Object> map = new HashMap<>();
		map.put(Tuples.of(), 1);
		map.put(Tuples.of(1), 1);
		map.put(Tuples.of(1, 2), 1);
		Assertions.assertEquals(map.size(), 3);
		Assertions.assertEquals(map.get(Tuples.of(1)), 1);
		Assertions.assertEquals(map.get(Tuples.of(1, 2)), 1);
	}
}

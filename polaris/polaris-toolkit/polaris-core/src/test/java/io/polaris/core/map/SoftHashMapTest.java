package io.polaris.core.map;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Map;

class SoftHashMapTest {
	@Data
	static class ValueObj {
		private final String val;
	}

	@Test
	public void test01() throws InterruptedException {
		// -Xmx8m
		Map<String, Object> map = new SoftHashMap<>();
		for (int i = 0; i < 100000; i++) {
			map.put("key-" + i, new ValueObj(""+i));
		}
		System.out.println(map.size());
		System.gc();
		Thread.sleep(100);
		System.out.println(map.size());

	}
	@Test
	public void test02() throws InterruptedException {
		// -Xmx8m
		Map<String, Object> map = new WeakHashMap<>();
		for (int i = 0; i < 100000; i++) {
			map.put("key-" + i, new ValueObj(""+i));
		}
		System.out.println(map.size());
		System.gc();
		Thread.sleep(100);
		System.out.println(map.size());

	}

}

package io.polaris.core.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;

/**
 * @author Qt
 * @since  May 06, 2024
 */
public class DefaultJsonSerializerTest {

	@Test
	void test03() {
		String json = "{\"a\":1,\"b\":2,\"c\":{\"c1\":3,\"c2\":4},\"d\":[1,2,3,4]}";
		Map map = new DefaultJsonSerializer().deserialize(json, Map.class);
		Consoles.println(map);
	}


	@Test
	void test04() {
		Map<String, Object> map = new HashMap<>();
		map.put("c", "123");
		map.put("a", "123");
		map.put("d", new ArrayList<>(Arrays.asList("a", "b", "c")));
		map.put("e", new String[]{"a", "b", "c"});
		String msg1 = new DefaultJsonSerializer().serialize(map);
		Consoles.println(msg1);
		String msg = JSON.toJSONString(map);
		Consoles.println(msg);
	}
}

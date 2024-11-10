package io.polaris.json;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.json.Jsons;
import io.polaris.core.random.Randoms;
import lombok.Data;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonsTest {

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

	@Test
	void test03() throws JsonProcessingException {
		ObjectMapper mapper = Jacksons.buildObjectMapper();
		mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
		mapper.activateDefaultTypingAsProperty(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.Id.NAME.getDefaultPropertyName());
		String json = mapper.writeValueAsString(new TestBean());
		System.out.println(json);
		System.out.println(mapper.readValue(json, Object.class));
		System.out.println(mapper.readValue(json, Object.class).getClass());
	}

	@Test
	void test04() throws JsonProcessingException {
		String json = Jacksons.toJsonString(Jacksons.autoTypeObjectMapper(), new TestBean());
		System.out.println(json);
		System.out.println(Jacksons.toJavaObject(Jacksons.autoTypeObjectMapper(), json, Object.class));
		System.out.println(Jacksons.toJavaObject(Jacksons.autoTypeObjectMapper(), json, Object.class).getClass());
	}

	@Data
	public static class TestBean {
		private String id = Randoms.randomString(6);
		private String name = Randoms.randomString(6);
		private TestBean1 testBean1 = new TestBean1();
		private TestBean1[] testBean1Arr = new TestBean1[]{new TestBean1(), new TestBean1()};
	}

	@Data
	public static class TestBean1 {
		private String id = Randoms.randomString(6);
		private String name = Randoms.randomString(6);
	}

}

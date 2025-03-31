package io.polaris.json.migration;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JSONTest {

	// 测试目标: 解析有效的 JSON 字符串为 JSONObject
	// 行为: 输入有效的 JSON 对象字符串，期望返回一个 JSONObject 对象，并与 fastjson2 的结果进行对比
	@Test
	public void testParseObjectFromValidString() {
		String jsonString = "{\"name\":\"John\", \"age\":30}";
		io.polaris.json.migration.JSONObject polarisJsonObject = JSON.parseObject(jsonString);
		com.alibaba.fastjson2.JSONObject fastjson2JsonObject = com.alibaba.fastjson2.JSON.parseObject(jsonString);

		assertNotNull(polarisJsonObject);
		assertNotNull(fastjson2JsonObject);
		assertEquals(fastjson2JsonObject.getString("name"), polarisJsonObject.getString("name"));
		assertEquals(fastjson2JsonObject.getIntValue("age"), polarisJsonObject.getIntValue("age"));
	}


	// 测试目标: 解析 JSON 字符串为指定类型的对象
	// 行为: 输入 JSON 字符串和目标类类型，期望返回指定类型的对象，并与 fastjson2 的结果进行对比
	@Test
	public void testParseObjectToSpecificType() {
		String jsonString = "{\"name\":\"John\", \"age\":30}";
		Person polarisPerson = JSON.parseObject(jsonString, Person.class);
		Person fastjson2Person = com.alibaba.fastjson2.JSON.parseObject(jsonString, Person.class);

		assertNotNull(polarisPerson);
		assertNotNull(fastjson2Person);
		assertEquals(fastjson2Person.getName(), polarisPerson.getName());
		assertEquals(fastjson2Person.getAge(), polarisPerson.getAge());
	}

	// 测试目标: 解析 JSON 字节数组为 JSONObject
	// 行为: 输入有效的 JSON 字节数组，期望返回一个 JSONObject 对象，并与 fastjson2 的结果进行对比
	@Test
	public void testParseObjectFromByteArray() {
		byte[] jsonBytes = "{\"name\":\"John\", \"age\":30}".getBytes(StandardCharsets.UTF_8);
		io.polaris.json.migration.JSONObject polarisJsonObject = JSON.parseObject(jsonBytes);
		com.alibaba.fastjson2.JSONObject fastjson2JsonObject = com.alibaba.fastjson2.JSON.parseObject(jsonBytes);

		assertNotNull(polarisJsonObject);
		assertNotNull(fastjson2JsonObject);
		assertEquals(fastjson2JsonObject.getString("name"), polarisJsonObject.getString("name"));
		assertEquals(fastjson2JsonObject.getIntValue("age"), polarisJsonObject.getIntValue("age"));
	}

	// 测试目标: 解析 JSON 字符数组为 JSONObject
	// 行为: 输入有效的 JSON 字符数组，期望返回一个 JSONObject 对象，并与 fastjson2 的结果进行对比
	@Test
	public void testParseObjectFromCharArray() {
		char[] jsonChars = "{\"name\":\"John\", \"age\":30}".toCharArray();
		io.polaris.json.migration.JSONObject polarisJsonObject = JSON.parseObject(jsonChars);
		com.alibaba.fastjson2.JSONObject fastjson2JsonObject = com.alibaba.fastjson2.JSON.parseObject(new String(jsonChars));

		assertNotNull(polarisJsonObject);
		assertNotNull(fastjson2JsonObject);
		assertEquals(fastjson2JsonObject.getString("name"), polarisJsonObject.getString("name"));
		assertEquals(fastjson2JsonObject.getIntValue("age"), polarisJsonObject.getIntValue("age"));
	}

	// 测试目标: 解析 JSON 输入流为 JSONObject
	// 行为: 输入有效的 JSON 输入流，期望返回一个 JSONObject 对象，并与 fastjson2 的结果进行对比
	@Test
	public void testParseObjectFromInputStream() {
		String jsonString = "{\"name\":\"John\", \"age\":30}";
		io.polaris.json.migration.JSONObject polarisJsonObject = JSON.parseObject(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)));
		com.alibaba.fastjson2.JSONObject fastjson2JsonObject = com.alibaba.fastjson2.JSON.parseObject(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)));

		assertNotNull(polarisJsonObject);
		assertNotNull(fastjson2JsonObject);
		assertEquals(fastjson2JsonObject.getString("name"), polarisJsonObject.getString("name"));
		assertEquals(fastjson2JsonObject.getIntValue("age"), polarisJsonObject.getIntValue("age"));
	}

	// 测试目标: 解析 JSON 字符串为 JSONArray
	// 行为: 输入有效的 JSON 数组字符串，期望返回一个 JSONArray 对象，并与 fastjson2 的结果进行对比
	@Test
	public void testParseArrayFromValidString() {
		String jsonArrayString = "[{\"name\":\"John\", \"age\":30}, {\"name\":\"Jane\", \"age\":25}]";
		io.polaris.json.migration.JSONArray polarisArray = JSON.parseArray(jsonArrayString);
		com.alibaba.fastjson2.JSONArray fastjson2Array = com.alibaba.fastjson2.JSON.parseArray(jsonArrayString);

		assertNotNull(polarisArray);
		assertNotNull(fastjson2Array);
		assertEquals(fastjson2Array.size(), polarisArray.size());
		assertEquals(fastjson2Array.getJSONObject(0).getString("name"), polarisArray.getJSONObject(0).getString("name"));
		assertEquals(fastjson2Array.getJSONObject(0).getIntValue("age"), polarisArray.getJSONObject(0).getIntValue("age"));
		assertEquals(fastjson2Array.getJSONObject(1).getString("name"), polarisArray.getJSONObject(1).getString("name"));
		assertEquals(fastjson2Array.getJSONObject(1).getIntValue("age"), polarisArray.getJSONObject(1).getIntValue("age"));
	}

	// 测试目标: 解析 JSON 字节数组为 JSONArray
	// 行为: 输入有效的 JSON 数组字节数组，期望返回一个 JSONArray 对象，并与 fastjson2 的结果进行对比
	@Test
	public void testParseArrayFromByteArray() {
		byte[] jsonArrayBytes = "[{\"name\":\"John\", \"age\":30}, {\"name\":\"Jane\", \"age\":25}]".getBytes(StandardCharsets.UTF_8);
		io.polaris.json.migration.JSONArray polarisArray = JSON.parseArray(jsonArrayBytes);
		com.alibaba.fastjson2.JSONArray fastjson2Array = com.alibaba.fastjson2.JSON.parseArray(jsonArrayBytes);

		assertNotNull(polarisArray);
		assertNotNull(fastjson2Array);
		assertEquals(fastjson2Array.size(), polarisArray.size());
		assertEquals(fastjson2Array.getJSONObject(0).getString("name"), polarisArray.getJSONObject(0).getString("name"));
		assertEquals(fastjson2Array.getJSONObject(0).getIntValue("age"), polarisArray.getJSONObject(0).getIntValue("age"));
		assertEquals(fastjson2Array.getJSONObject(1).getString("name"), polarisArray.getJSONObject(1).getString("name"));
		assertEquals(fastjson2Array.getJSONObject(1).getIntValue("age"), polarisArray.getJSONObject(1).getIntValue("age"));
	}

	// 测试目标: 解析 JSON 字符数组为 JSONArray
	// 行为: 输入有效的 JSON 数组字符数组，期望返回一个 JSONArray 对象，并与 fastjson2 的结果进行对比
	@Test
	public void testParseArrayFromCharArray() {
		char[] jsonArrayChars = "[{\"name\":\"John\", \"age\":30}, {\"name\":\"Jane\", \"age\":25}]".toCharArray();
		io.polaris.json.migration.JSONArray polarisArray = JSON.parseArray(jsonArrayChars);
		com.alibaba.fastjson2.JSONArray fastjson2Array = com.alibaba.fastjson2.JSON.parseArray(new String(jsonArrayChars));

		assertNotNull(polarisArray);
		assertNotNull(fastjson2Array);
		assertEquals(fastjson2Array.size(), polarisArray.size());
		assertEquals(fastjson2Array.getJSONObject(0).getString("name"), polarisArray.getJSONObject(0).getString("name"));
		assertEquals(fastjson2Array.getJSONObject(0).getIntValue("age"), polarisArray.getJSONObject(0).getIntValue("age"));
		assertEquals(fastjson2Array.getJSONObject(1).getString("name"), polarisArray.getJSONObject(1).getString("name"));
		assertEquals(fastjson2Array.getJSONObject(1).getIntValue("age"), polarisArray.getJSONObject(1).getIntValue("age"));
	}

	// 测试目标: 解析 JSON 输入流为 JSONArray
	// 行为: 输入有效的 JSON 数组输入流，期望返回一个 JSONArray 对象，并与 fastjson2 的结果进行对比
	@Test
	public void testParseArrayFromInputStream() {
		String jsonArrayString = "[{\"name\":\"John\", \"age\":30}, {\"name\":\"Jane\", \"age\":25}]";
		io.polaris.json.migration.JSONArray polarisArray = JSON.parseArray(new ByteArrayInputStream(jsonArrayString.getBytes(StandardCharsets.UTF_8)));
		com.alibaba.fastjson2.JSONArray fastjson2Array = com.alibaba.fastjson2.JSON.parseArray(new ByteArrayInputStream(jsonArrayString.getBytes(StandardCharsets.UTF_8)));

		assertNotNull(polarisArray);
		assertNotNull(fastjson2Array);
		assertEquals(fastjson2Array.size(), polarisArray.size());
		assertEquals(fastjson2Array.getJSONObject(0).getString("name"), polarisArray.getJSONObject(0).getString("name"));
		assertEquals(fastjson2Array.getJSONObject(0).getIntValue("age"), polarisArray.getJSONObject(0).getIntValue("age"));
		assertEquals(fastjson2Array.getJSONObject(1).getString("name"), polarisArray.getJSONObject(1).getString("name"));
		assertEquals(fastjson2Array.getJSONObject(1).getIntValue("age"), polarisArray.getJSONObject(1).getIntValue("age"));
	}

	// 测试目标: 将 Java 对象转换为 JSON 字符串
	// 行为: 输入一个 Java 对象，期望返回一个 JSON 字符串，并与 fastjson2 的结果进行对比
	@Test
	public void testToJSONString() {
		Person person = new Person("John", 30);
		String polarisJsonString = JSON.toJSONString(person);
		String fastjson2JsonString = com.alibaba.fastjson2.JSON.toJSONString(person);

		assertNotNull(polarisJsonString);
		assertNotNull(fastjson2JsonString);
		assertEquals(fastjson2JsonString, polarisJsonString);
	}

	// 测试目标: 将 Java 对象转换为 JSON 字节数组
	// 行为: 输入一个 Java 对象，期望返回一个 JSON 字节数组，并与 fastjson2 的结果进行对比
	@Test
	public void testToJSONBytes() {
		Person person = new Person("John", 30);
		byte[] polarisJsonBytes = JSON.toJSONBytes(person);
		byte[] fastjson2JsonBytes = JSON.toJSONBytes(person);

		assertNotNull(polarisJsonBytes);
		assertNotNull(fastjson2JsonBytes);
		assertArrayEquals(fastjson2JsonBytes, polarisJsonBytes);
	}

	// 测试目标: 将 Java 对象转换为 JSON 对象
	// 行为: 输入一个 Java 对象，期望返回一个 JSON 对象，并与 fastjson2 的结果进行对比
	@Test
	public void testToJSONObject() {
		Person person = new Person("John", 30);
		io.polaris.json.migration.JSONObject polarisJsonObject = (io.polaris.json.migration.JSONObject) JSON.toJSON(person);
		com.alibaba.fastjson2.JSONObject fastjson2JsonObject = com.alibaba.fastjson2.JSON.parseObject(JSON.toJSONString(person));

		assertNotNull(polarisJsonObject);
		assertNotNull(fastjson2JsonObject);
		assertEquals(fastjson2JsonObject.getString("name"), polarisJsonObject.getString("name"));
		assertEquals(fastjson2JsonObject.getIntValue("age"), polarisJsonObject.getIntValue("age"));
	}

	// 测试目标: 将 Java 对象转换为指定类型的对象
	// 行为: 输入一个 JSON 对象和目标类类型，期望返回指定类型的对象，并与 fastjson2 的结果进行对比
	@Test
	public void testToObject() {
		io.polaris.json.migration.JSONObject polarisJsonObject = new io.polaris.json.migration.JSONObject();
		polarisJsonObject.put("name", "John");
		polarisJsonObject.put("age", 30);

		com.alibaba.fastjson2.JSONObject fastjson2JsonObject = new com.alibaba.fastjson2.JSONObject();
		fastjson2JsonObject.put("name", "John");
		fastjson2JsonObject.put("age", 30);

		Person polarisPerson = JSON.to(Person.class, polarisJsonObject);
		Person fastjson2Person = JSON.to(Person.class, fastjson2JsonObject);

		assertNotNull(polarisPerson);
		assertNotNull(fastjson2Person);
		assertEquals(fastjson2Person.getName(), polarisPerson.getName());
		assertEquals(fastjson2Person.getAge(), polarisPerson.getAge());
	}

	// 辅助类
	static class Person {
		@JsonProperty(index = 1)
		private String name;
		@JsonProperty(index = 2)
		private int age;

		public Person() {}

		public Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}
}

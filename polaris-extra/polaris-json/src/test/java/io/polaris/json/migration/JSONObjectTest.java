package io.polaris.json.migration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONObjectTest {

	private JSONObject jsonObject;
	private com.alibaba.fastjson2.JSONObject fastJSONObject;

	@BeforeEach
	public void setUp() {
		Map<String, Object> initialData = new HashMap<>();
		initialData.put("name", "Alice");
		initialData.put("age", 35);
		initialData.put("isMember", true);
		initialData.put("tags", Arrays.asList("test", "dev"));
		initialData.put("nested", new HashMap<String, Object>() {{
			put("id", 123);
			put("price", new BigDecimal("100.99"));
		}});

		jsonObject = new JSONObject(initialData, false);
		fastJSONObject = new com.alibaba.fastjson2.JSONObject(initialData);
	}

	// region 基础操作测试
	@Test
	public void testPut() {
		// 测试目标: 验证添加键值对功能
		// 行为: 添加新键 "country" 并验证值是否正确
		jsonObject.put("country", "China");
		fastJSONObject.put("country", "China");
		assertEquals("China", jsonObject.getString("country"));
		assertEquals("China", fastJSONObject.getString("country"));
	}

	@Test
	public void testRemove() {
		// 测试目标: 验证删除键值对功能
		// 行为: 删除键 "age" 并验证是否移除成功
		jsonObject.remove("age");
		fastJSONObject.remove("age");
		assertNull(jsonObject.get("age"));
		assertNull(fastJSONObject.get("age"));
	}

	@Test
	public void testContainsKey() {
		// 测试目标: 验证键存在性检查
		// 行为: 检查键 "name" 是否存在
		assertTrue(jsonObject.containsKey("name"));
		assertTrue(fastJSONObject.containsKey("name"));
	}

	@Test
	public void testSize() {
		// 测试目标: 验证键值对数量
		// 行为: 获取当前对象的键值对总数
		assertEquals(5, jsonObject.size());
		assertEquals(5, fastJSONObject.size());
	}

	@Test
	public void testClear() {
		// 测试目标: 验证清空所有键值对功能
		// 行为: 清空对象后验证是否为空
		jsonObject.clear();
		fastJSONObject.clear();
		assertTrue(jsonObject.isEmpty());
		assertTrue(fastJSONObject.isEmpty());
	}
	// endregion

	// region 类型转换测试
	@Test
	public void testGetString() {
		// 测试目标: 验证字符串类型转换
		// 行为: 获取字符串类型的键 "name" 的值
		assertEquals("Alice", jsonObject.getString("name"));
		assertEquals("Alice", fastJSONObject.getString("name"));
	}

	@Test
	public void testGetIntValue() {
		// 测试目标: 验证整数类型转换
		// 行为: 获取整数类型的键 "age" 的值
		assertEquals(35, jsonObject.getIntValue("age"));
		assertEquals(35, fastJSONObject.getIntValue("age"));
	}

	@Test
	public void testGetBooleanValue() {
		// 测试目标: 验证布尔类型转换
		// 行为: 获取布尔类型的键 "isMember" 的值
		assertTrue(jsonObject.getBooleanValue("isMember"));
		assertTrue(fastJSONObject.getBooleanValue("isMember"));
	}

	@Test
	public void testGetJSONArray() {
		// 测试目标: 验证数组类型转换
		// 行为: 获取数组类型的键 "tags" 并验证元素
		JSONArray jsonArray = jsonObject.getJSONArray("tags");
		com.alibaba.fastjson2.JSONArray fastJsonArray = fastJSONObject.getJSONArray("tags");
		assertNotNull(jsonArray);
		assertNotNull(fastJsonArray);
		assertEquals(2, jsonArray.size());
		assertEquals(2, fastJsonArray.size());
		assertEquals("test", jsonArray.getString(0));
		assertEquals("test", fastJsonArray.getString(0));
	}

	@Test
	public void testGetJSONObject() {
		// 测试目标: 验证嵌套对象转换
		// 行为: 获取嵌套对象 "nested" 并验证其内容
		JSONObject nestedObj = jsonObject.getJSONObject("nested");
		com.alibaba.fastjson2.JSONObject nestedFastObj = fastJSONObject.getJSONObject("nested");
		assertNotNull(nestedObj);
		assertNotNull(nestedFastObj);
		assertEquals(123, nestedObj.getIntValue("id"));
		assertEquals(123, nestedFastObj.getIntValue("id"));
	}

	@Test
	public void testGetBigDecimal() {
		// 测试目标: 验证大数类型转换
		// 行为: 获取 BigDecimal 类型的值
		JSONObject nestedObj = jsonObject.getJSONObject("nested");
		com.alibaba.fastjson2.JSONObject nestedFastObj = fastJSONObject.getJSONObject("nested");
		BigDecimal expected = new BigDecimal("100.99");
		assertEquals(expected, nestedObj.getBigDecimal("price"));
		assertEquals(expected, nestedFastObj.getBigDecimal("price"));
	}

	@Test
	public void testGetInstant() {
		// 测试目标: 验证时间类型转换
		// 行为: 将时间戳转换为 Instant 类型
		jsonObject.put("timestamp", 1711420800000L);
		fastJSONObject.put("timestamp", 1711420800000L);
		Instant instant = jsonObject.getInstant("timestamp");
		Instant fastInstant = fastJSONObject.getInstant("timestamp");
		assertEquals(instant, fastInstant);
	}
	// endregion

	// region 静态方法测试
	@Test
	public void testParseObject() {
		// 测试目标: 验证 JSON 字符串解析
		// 行为: 解析标准 JSON 字符串为对象
		String json = "{\"user\":\"Bob\", \"score\":85}";
		JSONObject parsedObj = JSONObject.parseObject(json);
		com.alibaba.fastjson2.JSONObject parsedFastObj = com.alibaba.fastjson2.JSONObject.parseObject(json);
		assertEquals("Bob", parsedObj.getString("user"));
		assertEquals("Bob", parsedFastObj.getString("user"));
		assertEquals(85, parsedObj.getIntValue("score"));
		assertEquals(85, parsedFastObj.getIntValue("score"));
	}

	// endregion

	// region 特殊场景测试
	@Test
	public void testNullValueHandling() {
		// 测试目标: 验证空值处理
		// 行为: 获取不存在的键并验证返回 null
		assertNull(jsonObject.getString("unknown"));
		assertNull(fastJSONObject.getString("unknown"));
	}

	@Test
	public void testTypeConversionError() {
		// 测试目标: 验证类型转换异常
		// 行为: 尝试将非数字字符串转换为整数
		jsonObject.put("invalid", new Object());
		fastJSONObject.put("invalid", new Object());
		assertThrows(JSONException.class, () -> jsonObject.getIntValue("invalid"));
		assertThrows(com.alibaba.fastjson2.JSONException.class, () -> fastJSONObject.getIntValue("invalid"));
	}
	// endregion
}

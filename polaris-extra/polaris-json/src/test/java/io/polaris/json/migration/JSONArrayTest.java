package io.polaris.json.migration;

import java.util.List;

import io.polaris.core.assertion.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JSONArrayTest {

	private JSONArray ours;
	private com.alibaba.fastjson2.JSONArray theirs;

	@BeforeEach
	void setup() {
		String jsonStr = "[1, \"test\", true, [\"nested\"]]";
		ours = JSONArray.parseArray(jsonStr);
		theirs = com.alibaba.fastjson2.JSONArray.parseArray(jsonStr);
	}

	@Test
	void testParseArray_Basic() {
		// 测试目标：验证JSON字符串解析能力
		// 行为对比：与Fastjson2的parseArray方法解析结果对比
		assertEquals(ours.size(), theirs.size());
		for (int i = 0; i < ours.size(); i++) {
			assertEquals(ours.get(i),theirs.get(i));
		}
	}

	@Test
	void testGetString() {
		// 测试目标：验证字符串类型转换
		// 行为对比：与Fastjson2的getString方法对比
		String expected = "test";
		assertEquals(ours.getString(1),expected);
		assertEquals(theirs.getString(1),expected);
	}

	@Test
	void testGetJSONArray() {
		// 测试目标：验证嵌套数组解析
		// 行为对比：与Fastjson2的getJSONArray方法对比
		JSONArray nestedOurs = ours.getJSONArray(3);
		com.alibaba.fastjson2.JSONArray nestedTheirs = theirs.getJSONArray(3);
		assertEquals(nestedOurs.size(),nestedTheirs.size());
		assertEquals(nestedOurs.getString(0),nestedTheirs.getString(0));
	}

	@Test
	void testAdd_Fluent() {
		// 测试目标：验证链式操作
		// 行为对比：Fastjson2无此特性，仅验证功能实现
		ours.fluentAdd("newItem").fluentAdd(123);
		assertEquals(6, ours.size());
		assertEquals("newItem", ours.get(4));
	}

	@Test
	void testToJavaList() {
		// 测试目标：验证类型转换为Java List
		// 行为对比：与Fastjson2的toList方法对比
		List<Object> oursList = ours.toJavaList(Object.class);
		List<Object> theirsList = theirs.toList(Object.class);
		assertTrue(oursList.containsAll(theirsList) && theirsList.containsAll(oursList));
	}


	@Test
	void testGetDouble_Value() {
		// 测试目标：验证数值类型转换
		// 行为对比：与Fastjson2的getDouble方法对比
		double oursVal = ours.getDoubleValue(0);
		double theirsVal = theirs.getDoubleValue(0);
		assertEquals(oursVal,theirsVal);
	}

	@Test
	void testToString() {
		// 测试目标：验证JSON序列化一致性
		// 行为对比：与Fastjson2的toString方法对比
		assertEquals(ours.toJSONString(),theirs.toJSONString());
	}
}

package io.polaris.core.lang;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Qt
 */
class ObjsTest {

	@Test
	void testIsEmptyBeanAllFieldsNull() {
		// 所有属性均为 null，视为空
		DemoBean bean = new DemoBean();
		assertTrue(Objs.isEmptyBean(bean));
	}

	@Test
	void testIsEmptyBeanWithNonNullField() {
		// 存在非空属性，视为非空
		DemoBean bean = new DemoBean();
		bean.name = "abc";
		assertFalse(Objs.isEmptyBean(bean));
	}

	@Test
	void testIsEmptyJdkClassKeepOriginalBehavior() {
		// JDK 基础库类不按属性判空，保持原行为（返回 false）
		assertFalse(Objs.isEmptyBean(BigDecimal.valueOf(0)));
		assertFalse(Objs.isEmptyBean(new Date(0)));
	}

	@Test
	void testIsEmptyNoInstanceFieldBean() {
		// 没有任何属性的 Bean 视为空
		assertTrue(Objs.isEmptyBean(new EmptyBean()));
	}

	@Test
	void testisEmptyPrimitiveContainersStillWork() {
		// 基础类型（字符串/集合/Map/数组）的判空行为保持不变
		assertTrue(Objs.isEmptyBean(""));
		assertTrue(Objs.isEmptyBean(new ArrayList<>()));
		assertTrue(Objs.isEmptyBean(new HashMap<>()));
		assertTrue(Objs.isEmptyBean(new Object[0]));
		assertFalse(Objs.isEmptyBean("abc"));
	}

	/**
	 * 测试用 Bean，字段公开以便赋值。
	 */
	@SuppressWarnings("unused")
	public static class DemoBean {
		public String id;
		public String name;
		public Integer age;
		public List<String> tags;
		public Map<String, Object> attrs;
		public String[] codes;
	}

	/**
	 * 测试用嵌套 Bean，用于验证内层对象的判空语义。
	 */
	@SuppressWarnings("unused")
	public static class NestedBean {
		public NestedBean inner;
		public Integer value;
	}

	/**
	 * 无任何属性的 Bean。
	 */
	public static class EmptyBean {
	}
}

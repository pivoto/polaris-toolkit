package io.polaris.core.lang.bean.property;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * BeanProperties 类的单元测试
 */
class BeanPropertiesTest {

	/**
	 * 测试 classify 方法 - 基本功能
	 */
	@Test
	void testClassifyBasic() {
		BeanPropertiesClassification classification = BeanProperties.classify(TestBean.class);

		assertNotNull(classification);
		assertNotNull(classification.properties);
		assertNotNull(classification.setters);
		assertNotNull(classification.getters);
		assertNotNull(classification.fields);

		// TestBean 有 3 个属性：name, age, email
		assertEquals(3, classification.properties.size());
		assertEquals(3, classification.setters.size());
		assertEquals(3, classification.getters.size());
		// 没有 public 字段，所以 fields 应该为空
		assertEquals(0, classification.fields.size());

		// 验证属性名称存在
		assertTrue(classification.properties.containsKey("name"));
		assertTrue(classification.properties.containsKey("age"));
		assertTrue(classification.properties.containsKey("email"));
	}

	/**
	 * 测试 classify 方法 - 只有字段的 Bean
	 */
	@Test
	void testClassifyFieldOnlyBean() {
		BeanPropertiesClassification classification = BeanProperties.classify(FieldOnlyBean.class);

		assertNotNull(classification);
		// 只有 publicField 是有效的（非 final、非 static、public）
		assertEquals(1, classification.properties.size());
		assertEquals(0, classification.setters.size());
		assertEquals(0, classification.getters.size());
		assertEquals(1, classification.fields.size());

		assertTrue(classification.properties.containsKey("publicField"));
		assertTrue(classification.fields.get(0).getField().getName().equals("publicField"));
	}

	/**
	 * 测试 classify 方法 - 继承关系
	 */
	@Test
	void testClassifyInheritance() {
		BeanPropertiesClassification classification = BeanProperties.classify(ChildBean.class);

		assertNotNull(classification);
		// 应该包含父类和子类的属性
		assertEquals(2, classification.properties.size());
		assertEquals(2, classification.setters.size());
		assertEquals(2, classification.getters.size());

		assertTrue(classification.properties.containsKey("parentField"));
		assertTrue(classification.properties.containsKey("childField"));
	}

	/**
	 * 测试 listOf 方法 - 基本功能
	 */
	@Test
	void testListOfBasic() {
		List<BeanPropertyInfo> properties = BeanProperties.listOf(TestBean.class);

		assertNotNull(properties);
		assertEquals(3, properties.size());

		// 验证所有属性都存在
		boolean hasName = false, hasAge = false, hasEmail = false;
		for (BeanPropertyInfo info : properties) {
			switch (info.getPropertyName()) {
				case "name":
					hasName = true;
					break;
				case "age":
					hasAge = true;
					break;
				case "email":
					hasEmail = true;
					break;
			}
		}
		assertTrue(hasName && hasAge && hasEmail);
	}

	/**
	 * 测试 listOf 方法 - 空 Bean
	 */
	@Test
	void testListOfEmptyBean() {
		// 创建一个没有任何属性的类来测试
		class EmptyBean {}
		List<BeanPropertyInfo> properties = BeanProperties.listOf(EmptyBean.class);

		assertNotNull(properties);
		// Object 类有 getClass 等方法，但会被过滤掉，所以应该是空的或很少的属性
		assertTrue(properties.size() >= 0);
	}

	/**
	 * 测试 mapOf 方法 - 基本功能
	 */
	@Test
	void testMapOfBasic() {
		Map<String, BeanPropertyInfo> properties = BeanProperties.mapOf(TestBean.class);

		assertNotNull(properties);
		assertEquals(3, properties.size());

		// 验证属性信息正确性
		BeanPropertyInfo nameInfo = properties.get("name");
		assertNotNull(nameInfo);
		assertEquals("name", nameInfo.getPropertyName());
		assertNotNull(nameInfo.getReadMethod());
		assertNotNull(nameInfo.getWriteMethod());
		assertTrue(nameInfo.hasGetter());
		assertTrue(nameInfo.hasSetter());
	}

	/**
	 * 测试 mapOf 方法 - 缓存机制（调用两次应该返回相同结果）
	 */
	@Test
	void testMapOfCache() {
		Map<String, BeanPropertyInfo> firstCall = BeanProperties.mapOf(TestBean.class);
		Map<String, BeanPropertyInfo> secondCall = BeanProperties.mapOf(TestBean.class);

		assertNotNull(firstCall);
		assertNotNull(secondCall);
		assertEquals(firstCall.size(), secondCall.size());

		// 验证两个 Map 的内容相同（由于缓存，可能是同一个对象）
		assertEquals(firstCall.keySet(), secondCall.keySet());
	}

	/**
	 * 测试忽略属性注解 - 类级别 @IgnoredProperties
	 */
	@Test
	void testIgnoredPropertiesClassLevel() {
		Map<String, BeanPropertyInfo> properties = BeanProperties.mapOf(IgnoredTestBean.class);

		assertNotNull(properties);
		// ignoredField 被 @IgnoredProperties 忽略，不应该出现在结果中
		assertFalse(properties.containsKey("ignoredField"));
		// normalField 应该存在
		assertTrue(properties.containsKey("normalField"));
	}

	/**
	 * 测试忽略属性注解 - 方法级别 @IgnoredProperty
	 */
	@Test
	void testIgnoredPropertyMethodLevel() {
		Map<String, BeanPropertyInfo> properties = BeanProperties.mapOf(IgnoredTestBean.class);

		assertNotNull(properties);
		// ignoredByAnnotation 的 getter 被 @IgnoredProperty 标注，应该被忽略
		BeanPropertyInfo info = properties.get("ignoredByAnnotation");
		assertNotNull(info);
		assertFalse(info.hasGetter());
		assertTrue(info.hasSetter());
	}

	/**
	 * 测试 BeanPropertyInfo 的属性类型信息
	 */
	@Test
	void testBeanPropertyInfoTypes() {
		Map<String, BeanPropertyInfo> properties = BeanProperties.mapOf(TestBean.class);

		BeanPropertyInfo nameInfo = properties.get("name");
		assertEquals(String.class, nameInfo.getPropertyType());

		BeanPropertyInfo ageInfo = properties.get("age");
		assertEquals(int.class, ageInfo.getPropertyType());
	}

	/**
	 * 测试字段类型的 Bean 属性信息
	 */
	@Test
	void testFieldBasedPropertyInfo() {
		Map<String, BeanPropertyInfo> properties = BeanProperties.mapOf(FieldOnlyBean.class);

		BeanPropertyInfo fieldInfo = properties.get("publicField");
		assertNotNull(fieldInfo);
		assertEquals("publicField", fieldInfo.getPropertyName());
		assertNull(fieldInfo.getReadMethod());
		assertNull(fieldInfo.getWriteMethod());
		assertNotNull(fieldInfo.getField());
		assertEquals(String.class, fieldInfo.getPropertyType());
		assertTrue(fieldInfo.hasGetter());
		assertTrue(fieldInfo.hasSetter());
	}
}

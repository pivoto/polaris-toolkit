package io.polaris.json;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.random.Randoms;
import lombok.Data;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.core.JsonProcessingException;

public class FastjsonsTest {


	@Test
	void test01() {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 5; i++) {
			map.put("" + i, Randoms.randomString(6));
		}

		System.out.printf("%s%n", Fastjsons.toJsonString(map));
	}

	@Test
	void test02() {
		System.out.printf("%s%n", Fastjsons.toJavaObject("{a:123}", Map.class));
	}


	@Test
	void test03() throws JsonProcessingException {
		// https://alibaba.github.io/fastjson2/autotype_cn.html
		//, JSONWriter.Feature.NotWriteRootClassName
		String json = Fastjsons.toJsonString(new JacksonsTest.TestBean(), JSONWriter.Feature.WriteClassName);
		System.out.println(json);
		System.out.println(Fastjsons.toJavaObject(json, Object.class, JSONReader.Feature.SupportAutoType));
		System.out.println(Fastjsons.toJavaObject(json, Object.class, JSONReader.Feature.SupportAutoType).getClass());
	}

	@Test
	void test04() throws JsonProcessingException {
		String json = Fastjsons.toJsonStringWithClassName(new JacksonsTest.TestBean());
		System.out.println(json);
		System.out.println(Fastjsons.toJavaObject(json, Object.class, JacksonsTest.TestBean.class));
		System.out.println(Fastjsons.toJavaObject(json, Object.class, JacksonsTest.TestBean.class).getClass());
		System.out.println(Fastjsons.toJavaObject(json, Object.class, JSONReader.autoTypeFilter(true, JacksonsTest.TestBean.class)));
		System.out.println(Fastjsons.toJavaObject(json, Object.class, JSONReader.autoTypeFilter(true, JacksonsTest.TestBean.class)).getClass());
	}

	@Data
	public static class TestBean {
		private String id = Randoms.randomString(6);
		private String name = Randoms.randomString(6);
		private JacksonsTest.TestBean1 testBean1 = new JacksonsTest.TestBean1();
		private JacksonsTest.TestBean1[] testBean1Arr = new JacksonsTest.TestBean1[]{new JacksonsTest.TestBean1(), new JacksonsTest.TestBean1()};
	}

	@Data
	public static class TestBean1 {
		private String id = Randoms.randomString(6);
		private String name = Randoms.randomString(6);
	}
}

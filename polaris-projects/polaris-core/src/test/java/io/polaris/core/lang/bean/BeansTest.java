package io.polaris.core.lang.bean;

import io.polaris.core.time.Times;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class BeansTest {

	@Test
	void testBeanAccess() throws ReflectiveOperationException {
		Bean01 o = new Bean01();
		Map<String, Object> map = Beans.newBeanMap(o);
		System.out.println(map);
		System.out.println(o);
		map.put("id", "test");
		map.put("nickName", "test");

		System.out.println(map);
		System.out.println(o);

		map.forEach((k, v) -> {
			System.out.printf("%s -> %s%n", k, v);
		});
		System.out.println(map.get("nickName"));
	}

	@Test
	void test02() throws  Exception {
		Class<BeanMetadata> metadataClass = BeanMetadatas.getMetadataClass(Bean01.class);
		BeanMetadata iMetadata = metadataClass.newInstance();
		System.out.println(iMetadata.types());
		System.out.println(iMetadata.getters());
		System.out.println(iMetadata.setters());
	}

	@Test
	void test01() throws  Exception {
		Bean01 o = new Bean01();
		new BeanMap(o);
		long time = Times.millsTime(1000, () -> {
		BeanMap beanMap = new BeanMap(o);
		});
		System.out.println("time: " + time);
	}
}

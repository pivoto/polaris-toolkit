package io.polaris.core.lang.bean;

import io.polaris.core.io.Consoles;
import io.polaris.core.time.Times;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class BeansTest {

	@Test
	void testBeanMap() throws ReflectiveOperationException {
		Bean01 o = new Bean01();
		Map<String, Object> map = Beans.newBeanMap(o);
		map.put("id", "test");
		map.put("nickName", "test");

		Assertions.assertEquals(map.toString(), o.toString());

		map.forEach((k, v) -> {
			Consoles.println("{} -> {}", k, v);
		});
		Object[] args = new Object[]{map.get("nickName")};
		Consoles.println(args);
	}

	@Test
	void testBeanMetadatasV1() throws Exception {
		Class<BeanMetadataV1> metadataClass = BeanMetadatasV1.getMetadataClass(Bean01.class);
		BeanMetadataV1 iMetadata = metadataClass.newInstance();
		Object[] args2 = new Object[]{iMetadata.types()};
		Consoles.println(args2);
		Object[] args1 = new Object[]{iMetadata.getters()};
		Consoles.println(args1);
		Object[] args = new Object[]{iMetadata.setters()};
		Consoles.println(args);
	}

	@Test
	void test01() throws Exception {
		Bean01 o = new Bean01();
		Map<String, Object> map = Beans.newBeanMap(o);
		long time = Times.millsTime(1000, () -> {
			BeanMap beanMap = Beans.newBeanMap(o);
		});
		Consoles.println("time: " + time);
	}
}

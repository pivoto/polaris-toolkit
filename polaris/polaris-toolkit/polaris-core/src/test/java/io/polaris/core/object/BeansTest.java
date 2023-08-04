package io.polaris.core.object;

import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.BeanMetadata;
import io.polaris.core.lang.bean.BeanMetadatas;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.time.Times;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

public class BeansTest {
	@Data
	public static class Bean01 {
		private String id;
		private String name;
		private String nickName;
		private Bean01 raw;
		private Map<String, BigDecimal> numMap;
	}

	@Test
	void testBeanAccess() throws ReflectiveOperationException {
		Bean01 o = new Bean01();
		Map<String, Object> map = Beans.asBeanMap(o);
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

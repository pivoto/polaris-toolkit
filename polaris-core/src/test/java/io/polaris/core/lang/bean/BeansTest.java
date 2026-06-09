package io.polaris.core.lang.bean;

import io.polaris.core.io.Consoles;
import io.polaris.core.time.Times;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

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
			BeanMap<Bean01> beanMap = Beans.newBeanMap(o);
		});
		Consoles.println("time: " + time);
	}

	@Test
	void test02() throws Exception {
		CountDownLatch latch = new CountDownLatch(10);
		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				try {
					for (int j = 0; j < 100; j++) {
						long start = System.nanoTime();
						boolean isBean = Beans.isBeanClass(Bean01.class);
						long time = System.nanoTime() - start;
						Consoles.log("{}: {}. time: {} ns", j, isBean, time);
					}
				} finally {
					latch.countDown();
				}
			}).start();
		}
		latch.await();
	}
}

package io.polaris.core.lang.bean;

import io.polaris.core.random.Randoms;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MetaObjectTest {

	@Test
	void test01() {
		MetaObject<MetaT> meta = MetaObject.of(MetaT.class);
		MetaT target = meta.newInstance();
		{
			long val = System.currentTimeMillis() + 3600000;
			meta.setPathProperty(target, "id", val);
			meta.setPathProperty(target, "name", Randoms.randomString(10));
			meta.setPathProperty(target, "date", val);
			meta.setPathProperty(target, "date.a", val); // ignore

			meta.setPathProperty(target, "children.1.date", val);
			meta.setPathProperty(target, "children.3.date", val);
			meta.setPathProperty(target, "array.1.date", val);
			meta.setPathProperty(target, "array.3.date", val);
			meta.setPathProperty(target, "map.1.date", val);
			meta.setPathProperty(target, "map.3.date", val);
		}
		BeanMap<MetaT> beanMap = Beans.newBeanMap(target);
		System.out.println();
		beanMap.forEach((k, v) -> {
			System.out.println(k + " -> " + v);
		});
		System.out.println();
		System.out.println(meta.getPathProperty(target, "array.1"));
		System.out.println(meta.getPathProperty(target, "array.4"));
		System.out.println(meta.getPathProperty(target, "array.x"));


		System.out.println();
		System.out.println(Beans.newBeanMap(new Object()).keySet());
		System.out.println(Beans.newBeanMap(new Object[1]).keySet());
		System.out.println(Beans.newBeanMap(new ArrayList()).keySet());
	}

	@Data
	public static class MetaT {
		private Long id;
		private String name;
		private Date date;

		private List<MetaT> children;
		private MetaT[] array;
		private Map<Object, MetaT> map;
	}
}

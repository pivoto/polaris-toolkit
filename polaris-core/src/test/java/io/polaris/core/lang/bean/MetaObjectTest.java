package io.polaris.core.lang.bean;

import io.polaris.core.io.Consoles;
import io.polaris.core.random.Randoms;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaObjectTest {

	@Test
	void test01() {
		MetaObject<MetaT> meta = MetaObject.of(MetaT.class);
		MetaT target = meta.newInstance();
		long val = System.currentTimeMillis() + 3600000;
		{
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
		Consoles.println();
		beanMap.forEach((k, v) -> {
			Consoles.println(k + " -> " + v);
		});
		Consoles.println();
		Object[] args2 = new Object[]{meta.getPathProperty(target, "array.1")};
		Consoles.println("array.1 -> {}", args2);
		Object[] args1 = new Object[]{meta.getPathProperty(target, "array.4")};
		Consoles.println("array.4 -> {}", args1);
		Object[] args = new Object[]{meta.getPathProperty(target, "array.x")};
		Consoles.println("array.x -> {}", args);


		Consoles.println();
		Consoles.println(Beans.newBeanMap(new Object()).keySet());
		Consoles.println(Beans.newBeanMap(new Object[1]).keySet());
		Consoles.println(Beans.newBeanMap(new ArrayList()).keySet());
	}

	@Test
	void test02() {
		Map<String,Object> bindings = new HashMap<>();
		bindings.put("a", new HashMap<>());

		MetaObject<Map> meta = (MetaObject<Map>) MetaObject.of(bindings.getClass());
		meta.setPathProperty(bindings,"a.a","1");
		Consoles.println();
		Object[] args = new Object[]{meta.getPathProperty(bindings,"a.a")};
		Consoles.println(args);
		Consoles.println(bindings);
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

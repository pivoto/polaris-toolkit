package io.polaris.core.lang.bean;

import io.polaris.core.io.Consoles;
import io.polaris.core.json.Jsons;
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
		Consoles.println(()->Beans.newBeanMap(new Object()).keySet());
		Consoles.println(()->Beans.newBeanMap(new Object[1]).keySet());
		Consoles.println(()->Beans.newBeanMap(new ArrayList()).keySet());
	}

	@Test
	void test02() {
		Map<String,Object> bindings = new HashMap<>();

		MetaObject<Map<String,Object>> meta = (MetaObject<Map<String,Object>>) MetaObject.of(bindings.getClass());
		meta.setPathProperty(bindings,"a",new Map[1]);
		meta.setPathProperty(bindings,"a.0.x","1");
		meta.setPathProperty(bindings,"a.1",new HashMap<>());
		Consoles.println(bindings);
		Consoles.println(Jsons.serialize(bindings));
		meta.setPathProperty(bindings,"a.1.b","1");
		meta.setPathProperty(bindings,"a.1.c.d","1");
		meta.setPathProperty(bindings,"a.1.c",new HashMap<>());
		meta.setPathProperty(bindings,"a.1.c.d","1");

		Object[] args = new Object[]{meta.getPathProperty(bindings,"a.a")};
		Consoles.println(args);
		Consoles.println(bindings);
		Consoles.println(Jsons.serialize(bindings));

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

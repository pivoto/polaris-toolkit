package io.polaris.core.lang.copier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.polaris.core.collection.Lists;
import io.polaris.core.io.Consoles;
import io.polaris.core.json.Jsons;
import io.polaris.core.map.Maps;
import io.polaris.core.string.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Qt
 * @since Nov 15, 2024
 */
@SuppressWarnings("rawtypes")
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(ClassOrderer.ClassName.class)
public class CopiersTest03 {

	@Test
	void test01_clone_array() {
		Object source = new Object[]{1, 2, 3};

		Consoles.log(Copiers.deepClone(source, CopyOptions.DEFAULT));
		Consoles.log(Jsons.serialize(Copiers.deepClone(source, CopyOptions.DEFAULT)));
	}

	@Test
	void test01_copy_array() {
		Object source = new Object[]{1, 2, 3};
		Object target = new Object[5];

		Consoles.log(Copiers.deepCopy(source, target));
		Consoles.log(Jsons.serialize(Copiers.deepCopy(source, target)));
	}

	@Test
	void test02_clone_object() {
		Bean01 source = Bean01.builder().name("Jack").age(18).build();

		Consoles.log(Copiers.deepClone(source, CopyOptions.DEFAULT));
		Consoles.log(Jsons.serialize(Copiers.deepClone(source, CopyOptions.DEFAULT)));

	}

	@Test
	void test02_copy_object() {
		Bean01 source = Bean01.builder().name("Jack").age(18).build();
		Bean01 target = new Bean01();

		Consoles.log(Copiers.deepCopy(source, target));
		Consoles.log(Jsons.serialize(Copiers.deepCopy(source, target)));

	}

	@Test
	void test03_clone_array_object() {
		Object[] source = new Object[]{
			Bean01.builder().name("Jack").age(18).build(),
			Bean01.builder().name("Jack").age(18).build()
		};

		Consoles.log(Copiers.deepClone(source, CopyOptions.DEFAULT));
		Consoles.log(Jsons.serialize(Copiers.deepClone(source, CopyOptions.DEFAULT)));
	}

	@Test
	void test03_clone_copy_object() {
		Object[] source = new Object[]{
			Bean01.builder().name("Jack").age(18).build(),
			Bean01.builder().name("Jack").age(18).build()
		};

		Object[] target = new Object[5];

		Consoles.log(Copiers.deepCopy(source, target));
		Consoles.log(Jsons.serialize(Copiers.deepCopy(source, target)));
	}

	@Test
	void test04_clone_Map() {
		Object source = Maps.newFluentMap(new HashMap<>())
			.put("name", "Jack")
			.put("age", 18)
			.get();

		Consoles.log(Copiers.deepClone(source, CopyOptions.DEFAULT));
		Consoles.log(Jsons.serialize(Copiers.deepClone(source, CopyOptions.DEFAULT)));
	}

	@Test
	void test04_copy_Map() {
		Object source = Maps.newFluentMap(new HashMap<>())
			.put("name", "Jack")
			.put("age", 18)
			.get();
		Bean01 target = new Bean01();

		Consoles.log(Copiers.deepCopy(source, target));
		Consoles.log(Jsons.serialize(Copiers.deepCopy(source, target)));
	}


	@Test
	void test05() {
		Map<Object, Object> child = Maps.newFluentMap(new HashMap<>())
			.put("name", "Jack")
			.put("age", 18)
			.get();
		Object source = Maps.newFluentMap(new HashMap<>())
			.put("name", "Jack")
			.put("age", 55)
			.put("child", child)
			.put("children", Lists.asList(child, child))
			.get();
		Bean01 target = new Bean01();
		Consoles.log(target);
		Consoles.log(Jsons.serialize(target));


		Copiers.deepCopy(source, target);
		Consoles.log(Strings.repeat('-', 40));
		Consoles.log(Jsons.serialize(target));
		Consoles.log(target);

		Consoles.log(target.getChild() == (child));
		Consoles.log(target.getChild().equals(child));

	}


	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Bean01 {
		private String name;
		private Integer age;
		private Bean01 child;
		private List<Bean01> children;
	}

}

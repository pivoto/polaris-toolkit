package io.polaris.core.lang.copier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.polaris.core.io.Consoles;
import io.polaris.core.json.Jsons;
import io.polaris.core.map.Maps;
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
public class CopiersTest02 {

	@Test
	void test01() {
		Object[] source = new Object[5];
		source[0] = new HashMap<>();
		source[2] = new HashMap<>();
		Object[] target = new Object[5];


		Consoles.log("", Copiers.copy(source, target));
		Consoles.log("", Jsons.serialize(Copiers.copy(source, target)));

		Consoles.log("", Copiers.fastCopy(source, target));
		Consoles.log("", Jsons.serialize(Copiers.fastCopy(source, target)));
	}


	@Test
	void test02() {
		Object[] source = new Object[5];
		source[0] = new HashMap<>();
		source[2] = new HashMap<>();
		List<?> target = new ArrayList<>();

		Consoles.log("", Copiers.copy(source, target));
		Consoles.log("", Jsons.serialize(Copiers.copy(source, target)));

		Consoles.log("", Copiers.fastCopy(source, target));
		Consoles.log("", Jsons.serialize(Copiers.fastCopy(source, target)));
	}


	@Test
	void test03() {
		List source = new ArrayList<>();
		source.add(Maps.newFluentMap(new HashMap<>()).put("a", 1).get());
		source.add(null);
		source.add(Maps.newFluentMap(new HashMap<>()).put("b", 2).get());
		List target = new ArrayList<>();

		Consoles.log("", Copiers.copy(source, target));
		Consoles.log("", Jsons.serialize(Copiers.copy(source, target)));

		Consoles.log("", Copiers.fastCopy(source, target));
		Consoles.log("", Jsons.serialize(Copiers.fastCopy(source, target)));
	}


	@Test
	void test04() {
		List source = new ArrayList<>();
		source.add(Maps.newFluentMap(new HashMap<>()).put("a", 1).get());
		source.add(null);
		source.add(Maps.newFluentMap(new HashMap<>()).put("b", 2).get());
		Object[] target = new Object[5];

		Consoles.log("", Copiers.copy(source, target));
		Consoles.log("", Jsons.serialize(Copiers.copy(source, target)));

		Consoles.log("", Copiers.fastCopy(source, target));
		Consoles.log("", Jsons.serialize(Copiers.fastCopy(source, target)));
	}


}

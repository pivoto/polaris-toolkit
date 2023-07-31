package io.polaris.core.converter;

import io.polaris.core.collection.Iterables;
import io.polaris.core.json.TestJsonSerializer;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.TypeRefs;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class ConverterRegistryTest {
	@Test
	void test01() {
		String json = "[1,2,3]";
		List<String> map = new TestJsonSerializer().deserialize(json, new TypeRef<List<String>>() {
		});
		System.out.println(map);
	}

	@Test
	void test02() {
		Object o = ConverterRegistry.INSTANCE.convertQuietly(new TypeRef<Map<String, List<String>>>() {
		}.getType(), Strings.asMap("k1", "['a','b']", "k2", "aaa"));
		System.out.printf("convert: %s%n", Iterables.toArrayString(o));
	}

	@Test
	void testConvert() throws ClassNotFoundException {
		{
			String o = ConverterRegistry.INSTANCE.convertQuietly(String.class, null);
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}
		{
			String o = ConverterRegistry.INSTANCE.convertQuietly(String.class, Strings.asMap("k1", "v1", "k2", "v2"));
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}
		{
			Object o = ConverterRegistry.INSTANCE.convertQuietly(new TypeRef<Map<String, List<String>>>() {
			}.getType(), Strings.asMap("k1", "['a','b']", "k2", "['a','b']"));
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}

		{
			Object o = ConverterRegistry.INSTANCE.convertQuietly(Map.class, ConverterRegistry.INSTANCE.convertQuietly(String.class, Strings.asMap("k1", "v1", "k2", "v2")));
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}

		{
			Object o = ConverterRegistry.INSTANCE.convertQuietly(TypeRefs.getType("java.util.List<String>"), "[1,2,3, 12]");
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}

		{
			String[] o = ConverterRegistry.INSTANCE.convertQuietly(TypeRefs.getType("java.lang.String[]"), "[1,2,3, 12]");
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}

	}
}


package io.polaris.core.converter;

import io.polaris.core.collection.Iterables;
import io.polaris.core.json.TestJsonSerializer;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.TypeRefs;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

class ConverterTest {
	@Test
	void test01() {
		String json = "[1,2,3]";
		List<String> map = new TestJsonSerializer().deserialize(json, new TypeRef<List<String>>() {
		});
		System.out.println(map);
	}

	@Test
	void test02() {
		Type type = new TypeRef<Map<String, List<String>>>() {
		}.getType();
		Object value = Strings.asMap("k1", "['a','b']", "k2", "aaa");
		Object o = Converters.convertQuietly(type, value);
		System.out.printf("convert: %s%n", Iterables.toArrayString(o));
	}

	@Test
	void testConvert() throws ClassNotFoundException {
		{
			String o = Converters.convertQuietly(String.class, null);
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}
		{
			Object value = Strings.asMap("k1", "v1", "k2", "v2");
			String o = Converters.convertQuietly(String.class, value);
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}
		{
			Type type = new TypeRef<Map<String, List<String>>>() {
			}.getType();
			Object value = Strings.asMap("k1", "['a','b']", "k2", "['a','b']");
			Object o = Converters.convertQuietly(type, value);
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}

		{
			Object value = Strings.asMap("k1", "v1", "k2", "v2");
			Object value1 = Converters.convertQuietly(String.class, value);
			Object o = Converters.convertQuietly(Map.class, value1);
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}

		{
			Type type = TypeRefs.getType("java.util.List<String>");
			Object o = Converters.convertQuietly(type, "[1,2,3, 12]");
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}

		{
			Type type = TypeRefs.getType("java.lang.String[]");
			String[] o = Converters.convertQuietly(type, "[1,2,3, 12]");
			System.out.printf("convert: %s%n", Iterables.toArrayString(o));
		}

	}
}


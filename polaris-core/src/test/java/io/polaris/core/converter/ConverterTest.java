package io.polaris.core.converter;

import io.polaris.core.collection.Iterables;
import io.polaris.core.io.Consoles;
import io.polaris.core.json.TestJsonSerializer;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.TypeRefs;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

class ConverterTest {
	@Test
	void test01() {
		String json = "[1,2,3]";
		List<String> obj = new TestJsonSerializer().deserialize(json, new TypeRef<List<String>>() {
		});
		Consoles.println("obj: {}", obj);
		Assertions.assertNotNull(obj);
		Assertions.assertEquals(3, obj.size());
	}

	@SuppressWarnings("rawtypes")
	@Test
	void test02() {
		Type type = new TypeRef<Map<String, List<String>>>() {
		}.getType();
		Object value = Strings.asMap("k1", "['a','b']", "k2", "[\"aaa\"]");
		Object o = Converters.convertQuietly(type, value);
		Assertions.assertInstanceOf(Map.class, o);
		Assertions.assertInstanceOf(List.class, ((Map)o).get("k1"));
		Assertions.assertInstanceOf(List.class, ((Map)o).get("k2"));
		Object[] args = new Object[]{Iterables.toArrayString(o)};
		Consoles.println("convert: {}", args);
	}

	@SuppressWarnings("rawtypes")
	@Test
	void testConvert() throws ClassNotFoundException {
		{
			String o = Converters.convertQuietly(String.class, null);
			Assertions.assertNull(o);
			Object[] args = new Object[]{Iterables.toArrayString(o)};
			Consoles.println("convert: {}", args);
		}
		{
			Object value = Strings.asMap("k1", "v1", "k2", "v2");
			String o = Converters.convertQuietly(String.class, value);
			Assertions.assertNotNull(o);
			Assertions.assertTrue(o.startsWith("{"));
			Assertions.assertTrue(o.endsWith("}"));
			Object[] args = new Object[]{Iterables.toArrayString(o)};
			Consoles.println("convert: {}", args);
		}
		{
			Type type = new TypeRef<Map<String, List<String>>>() {
			}.getType();
			Object value = Strings.asMap("k1", "['a','b']", "k2", "['a','b']");
			Object o = Converters.convertQuietly(type, value);
			Assertions.assertNotNull(o);
			Assertions.assertInstanceOf(Map.class, o);
			Assertions.assertInstanceOf(List.class, ((Map)o).get("k1"));
			Assertions.assertInstanceOf(List.class, ((Map)o).get("k2"));
			Object[] args = new Object[]{Iterables.toArrayString(o)};
			Consoles.println("convert: {}", args);
		}

		{
			Object value = Strings.asMap("k1", "v1", "k2", "v2");
			Object value1 = Converters.convertQuietly(String.class, value);
			Object o = Converters.convertQuietly(Map.class, value1);
			Assertions.assertNotNull(o);
			Assertions.assertInstanceOf(Map.class, o);
			Assertions.assertEquals(value, o);
			Object[] args = new Object[]{Iterables.toArrayString(o)};
			Consoles.println("convert: {}", args);
		}

		{
			Type type = TypeRefs.getType("java.util.List<String>");
			Object o = Converters.convertQuietly(type, "[1,2,3, 12]");
			Assertions.assertNotNull(o);
			Assertions.assertInstanceOf(List.class, o);
			Assertions.assertEquals(4, ((List)o).size());
			Object[] args = new Object[]{Iterables.toArrayString(o)};
			Consoles.println("convert: {}", args);
		}

		{
			Type type = TypeRefs.getType("java.lang.String[]");
			String[] o = Converters.convertQuietly(type, "[1,2,3, 12]");
			Assertions.assertNotNull(o);
			Assertions.assertInstanceOf(String[].class, o);
			Assertions.assertEquals(4, ((String[])o).length);
			Object[] args = new Object[]{Iterables.toArrayString(o)};
			Consoles.println("convert: {}", args);
		}

	}
}


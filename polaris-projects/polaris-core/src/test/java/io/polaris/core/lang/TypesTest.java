package io.polaris.core.lang;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TypesTest {

	@Test
	void testGetClass() throws ClassNotFoundException {
		{
			Type type = TypeRefs.getType("java.util.Map<String[][][],? extends java.util.Map<String,String>>");
			TestConsole.println(type.getClass());
			TestConsole.println(type.getTypeName());
			Assertions.assertEquals("java.util.Map<java.lang.String[][][], ? extends java.util.Map<java.lang.String, java.lang.String>>", type.getTypeName());
			TestConsole.println(Types.getClass(type));
			if (type instanceof ParameterizedType) {
				TestConsole.println(Types.getClass(((ParameterizedType) type).getActualTypeArguments()[0]));
				TestConsole.println(Types.getClass(((ParameterizedType) type).getActualTypeArguments()[1]));

				Assertions.assertEquals(String[][][].class, Types.getClass(((ParameterizedType) type).getActualTypeArguments()[0]));
				Assertions.assertEquals(Map.class, Types.getClass(((ParameterizedType) type).getActualTypeArguments()[1]));
			}
		}
	}


	@Test
	void test01() {
		{
			JavaType t = JavaType.of(A.class);
			TestConsole.println(t.getTypeVariableMap());
			TestConsole.println(t.getActualType(List.class, 0));
			TestConsole.println(t.getActualType(Collection.class, 0));
		}
		{
			JavaType t = JavaType.of(new TypeRef<List<? extends List<String>>>() {
			});
			TestConsole.println(t.getTypeVariableMap());
			TestConsole.println(t.getActualType(List.class, 0));
			TestConsole.println(t.getActualType(Collection.class, 0));
		}
	}

	static class A extends ArrayList<String> {
	}
}

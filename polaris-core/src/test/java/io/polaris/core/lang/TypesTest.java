package io.polaris.core.lang;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TypesTest {

	@Test
	void testGetClass() throws ClassNotFoundException {
		{
			Type type = TypeRefs.getType("java.util.Map<String[][][],? extends java.util.Map<String,String>>");
			Consoles.println(type.getClass());
			String msg = type.getTypeName();
			Consoles.println(msg);
			Assertions.assertEquals("java.util.Map<java.lang.String[][][], ? extends java.util.Map<java.lang.String, java.lang.String>>", type.getTypeName());
			Object[] args2 = new Object[]{Types.getClass(type)};
			Consoles.println(args2);
			if (type instanceof ParameterizedType) {
				Object[] args1 = new Object[]{Types.getClass(((ParameterizedType) type).getActualTypeArguments()[0])};
				Consoles.println(args1);
				Object[] args = new Object[]{Types.getClass(((ParameterizedType) type).getActualTypeArguments()[1])};
				Consoles.println(args);

				Assertions.assertEquals(String[][][].class, Types.getClass(((ParameterizedType) type).getActualTypeArguments()[0]));
				Assertions.assertEquals(Map.class, Types.getClass(((ParameterizedType) type).getActualTypeArguments()[1]));
			}
		}
	}


	@Test
	void test01() {
		{
			JavaType t = JavaType.of(A.class);
			Object[] args2 = new Object[]{t.getTypeVariableMap()};
			Consoles.println(args2);
			Object[] args1 = new Object[]{t.getActualType(List.class, 0)};
			Consoles.println(args1);
			Object[] args = new Object[]{t.getActualType(Collection.class, 0)};
			Consoles.println(args);
		}
		{
			JavaType t = JavaType.of(new TypeRef<List<? extends List<String>>>() {
			});
			Object[] args2 = new Object[]{t.getTypeVariableMap()};
			Consoles.println(args2);
			Object[] args1 = new Object[]{t.getActualType(List.class, 0)};
			Consoles.println(args1);
			Object[] args = new Object[]{t.getActualType(Collection.class, 0)};
			Consoles.println(args);
		}
	}

	static class A extends ArrayList<String> {
	}
}

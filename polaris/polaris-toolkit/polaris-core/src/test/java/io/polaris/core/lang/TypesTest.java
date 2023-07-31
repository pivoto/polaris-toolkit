package io.polaris.core.lang;

import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class TypesTest {

	@Test
	void testGetClass() throws ClassNotFoundException {
		{
			Type type = TypeRefs.getType("java.util.Map<String[][][],? extends java.util.Map<String,String>>");
			System.out.println(type.getClass());
			System.out.println(type.getTypeName());
			System.out.println(Types.getClass(type));
			if (type instanceof ParameterizedType) {
				System.out.println(Types.getClass(((ParameterizedType) type).getActualTypeArguments()[0]));
				System.out.println(Types.getClass(((ParameterizedType) type).getActualTypeArguments()[1]));
			}
		}
	}


	@Test
	void test01() {
		{
			JavaType t = JavaType.of(A.class);
			System.out.println(t.getTypeVariableMap());
			System.out.println(t.getActualType(List.class, 0));
			System.out.println(t.getActualType(Collection.class, 0));
		}
		{
			JavaType t = JavaType.of(new TypeRef<List<? extends List<String>>>() {
			});
			System.out.println(t.getTypeVariableMap());
			System.out.println(t.getActualType(List.class, 0));
			System.out.println(t.getActualType(Collection.class, 0));
		}
	}

	static class A extends ArrayList<String> {
	}
}

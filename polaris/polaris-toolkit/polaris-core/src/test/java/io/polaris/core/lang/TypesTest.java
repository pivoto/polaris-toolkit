package io.polaris.core.lang;

import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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


}

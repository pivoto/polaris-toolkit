package io.polaris.core.lang.copier;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

import io.polaris.core.TestConsole;
import io.polaris.core.lang.Types;
import io.polaris.core.reflect.Reflects;
import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since 1.8
 */
public class IntrospectorTest {

	@Test
	void test01() throws IntrospectionException {
		TestConsole.println(Arrays.toString(Introspector.getBeanInfoSearchPath()));

		BeanInfo beanInfo = Introspector.getBeanInfo(C3.class);
		for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
			TestConsole.println("C3: {}", pd);
		}

		TestConsole.println("toParameterizedType: {}", Types.toParameterizedType(C3.class));
		TestConsole.println("toParameterizedType: {}", Types.toParameterizedType(C2.class));
		TestConsole.println("toParameterizedType: {}", Types.toParameterizedType(C1.class));
		TestConsole.println("toParameterizedType: {}", Types.toParameterizedType(I3.class));
		TestConsole.println("toParameterizedType: {}", Types.toParameterizedType(I2.class));
		TestConsole.println("toParameterizedType: {}", Types.toParameterizedType(I1.class));

		TestConsole.println("getTypeVariableMap: {}", Types.getTypeVariableMap(C3.class));
		TestConsole.println("getTypeVariableMap: {}", Types.getTypeVariableMap(C2.class));
		TestConsole.println("getActualType: {}", Types.getActualType(C1.class, (TypeVariable<?>) Reflects.getMethodByName(C1.class, "getA").getGenericReturnType()));
	}

	interface I<A, B, C> {

		default A getA() {
			return null;
		}
	}

	interface I1<B, C> extends I<String, B, C> {
	}

	interface I2<C> extends I1<Integer, C> {
	}

	interface I3 extends I2<Short> {
	}

	@Data
	static class C1 implements I<Integer, String, Character> {}

	@Data
	static class C2 implements I3 {

		@Override
		public String getA() {
			return I3.super.getA();
		}
	}

	@Data
	static class C3<T> implements I2<T> {
		public T getT() {
			return null;
		}
	}

	@Data
	static class P {
		private String id;
	}


	@Data
	static class S extends P {
		private String name;
	}


}

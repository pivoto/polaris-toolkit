package io.polaris.annotation.processor;

import io.polaris.annotation.processing.BeanA;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
public class AnnoTest {

	@Test
	void test01() throws ClassNotFoundException {

		Consumer<Function<Object, Type>> consumer = new Consumer<Function<Object, Type>>() {
			@Override
			public void accept(Function<Object, Type> objectTypeFunction) {
			}
		};
		Type type = ((ParameterizedType) consumer.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
		System.out.println(type);

		System.out.println(Class.forName(BeanA.class.getName()+"Map"));
//		System.out.println(Bean1Fields.getId.apply(new Bean1()));

		Function<Object, Type> convert;
	}

	static class RelatedMap{

	}
}

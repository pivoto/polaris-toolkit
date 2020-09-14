package io.polaris.core.annotation.processing;

import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Function;


class DemoBeanTest {


	@Test
	void test01() throws ClassNotFoundException {

		Consumer<Function<Object, Type>> consumer = new Consumer<Function<Object, Type>>() {
			@Override
			public void accept(Function<Object, Type> objectTypeFunction) {
			}
		};
		Type type = ((ParameterizedType) consumer.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
		System.out.println(type);

		System.out.println(Class.forName(DemoBean.class.getName() + "Map"));
//		System.out.println(Bean1Fields.getId.apply(new Bean1()));
		Function<Object, Type> convert;
	}

	@Test
	void test02() {
		DemoBeanMap map = DemoBeanMap.of(new DemoBean());
		System.out.println(map.keySet());
		System.out.println(map);
		map.put("id", "0xff");
		System.out.println(map);
	}
}

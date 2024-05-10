package io.polaris.core.lang.bean.sub;

import io.polaris.core.TestConsole;
import io.polaris.core.lang.bean.IndexedMetaObject;
import io.polaris.core.lang.bean.LambdaMetaObject;
import io.polaris.core.lang.bean.MetaObjectTestBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since  Apr 12, 2024
 */
public class MetaObjectTest {

	@Test
	void testIndexedMetaObject() {

		IndexedMetaObject<MetaObjectTestBean> meta = IndexedMetaObject.of(MetaObjectTestBean.class);
		MetaObjectTestBean bean = new MetaObjectTestBean();

		meta.setProperty(bean, "privateStringVal", "test");
		Assertions.assertEquals("test", meta.getProperty(bean, "privateStringVal"));
		TestConsole.printx("$.privateStringVal: {}", meta.getProperty(bean, "privateStringVal"));

		meta.setProperty(bean, "publicStringVal", "test");
		Assertions.assertEquals("test", meta.getProperty(bean, "publicStringVal"));
		TestConsole.printx("$.publicStringVal: {}", meta.getProperty(bean, "publicStringVal"));

		meta.setProperty(bean, "protectedStringVal", "test");
		Assertions.assertNotEquals("test", meta.getProperty(bean, "protectedStringVal"));
		TestConsole.printx("$.protectedStringVal: {}", meta.getProperty(bean, "protectedStringVal"));

		meta.setProperty(bean, "defaultStringVal", "test");
		Assertions.assertNotEquals("test", meta.getProperty(bean, "defaultStringVal"));
		TestConsole.printx("$.defaultStringVal: {}", meta.getProperty(bean, "defaultStringVal"));

	}

	@Test
	void testLambdaMetaObject() {

		LambdaMetaObject<MetaObjectTestBean> meta = LambdaMetaObject.of(MetaObjectTestBean.class);
		MetaObjectTestBean bean = new MetaObjectTestBean();

		meta.setProperty(bean, "privateStringVal", "test");
		Assertions.assertEquals("test", meta.getProperty(bean, "privateStringVal"));
		TestConsole.printx("$.privateStringVal: {}", meta.getProperty(bean, "privateStringVal"));

		meta.setProperty(bean, "publicStringVal", "test");
		Assertions.assertEquals("test", meta.getProperty(bean, "publicStringVal"));
		TestConsole.printx("$.publicStringVal: {}", meta.getProperty(bean, "publicStringVal"));

		meta.setProperty(bean, "protectedStringVal", "test");
		Assertions.assertNotEquals("test", meta.getProperty(bean, "protectedStringVal"));
		TestConsole.printx("$.protectedStringVal: {}", meta.getProperty(bean, "protectedStringVal"));

		meta.setProperty(bean, "defaultStringVal", "test");
		Assertions.assertNotEquals("test", meta.getProperty(bean, "defaultStringVal"));
		TestConsole.printx("$.defaultStringVal: {}", meta.getProperty(bean, "defaultStringVal"));

	}
}

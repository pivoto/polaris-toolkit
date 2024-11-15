package io.polaris.core.lang.bean.sub;

import io.polaris.core.io.Consoles;
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
		Object[] args3 = new Object[]{meta.getProperty(bean, "privateStringVal")};
		Consoles.log("$.privateStringVal: {}", args3);

		meta.setProperty(bean, "publicStringVal", "test");
		Assertions.assertEquals("test", meta.getProperty(bean, "publicStringVal"));
		Object[] args2 = new Object[]{meta.getProperty(bean, "publicStringVal")};
		Consoles.log("$.publicStringVal: {}", args2);

		meta.setProperty(bean, "protectedStringVal", "test");
		Assertions.assertNotEquals("test", meta.getProperty(bean, "protectedStringVal"));
		Object[] args1 = new Object[]{meta.getProperty(bean, "protectedStringVal")};
		Consoles.log("$.protectedStringVal: {}", args1);

		meta.setProperty(bean, "defaultStringVal", "test");
		Assertions.assertNotEquals("test", meta.getProperty(bean, "defaultStringVal"));
		Object[] args = new Object[]{meta.getProperty(bean, "defaultStringVal")};
		Consoles.log("$.defaultStringVal: {}", args);

	}

	@Test
	void testLambdaMetaObject() {

		LambdaMetaObject<MetaObjectTestBean> meta = LambdaMetaObject.of(MetaObjectTestBean.class);
		MetaObjectTestBean bean = new MetaObjectTestBean();

		meta.setProperty(bean, "privateStringVal", "test");
		Assertions.assertEquals("test", meta.getProperty(bean, "privateStringVal"));
		Object[] args3 = new Object[]{meta.getProperty(bean, "privateStringVal")};
		Consoles.log("$.privateStringVal: {}", args3);

		meta.setProperty(bean, "publicStringVal", "test");
		Assertions.assertEquals("test", meta.getProperty(bean, "publicStringVal"));
		Object[] args2 = new Object[]{meta.getProperty(bean, "publicStringVal")};
		Consoles.log("$.publicStringVal: {}", args2);

		meta.setProperty(bean, "protectedStringVal", "test");
		Assertions.assertNotEquals("test", meta.getProperty(bean, "protectedStringVal"));
		Object[] args1 = new Object[]{meta.getProperty(bean, "protectedStringVal")};
		Consoles.log("$.protectedStringVal: {}", args1);

		meta.setProperty(bean, "defaultStringVal", "test");
		Assertions.assertNotEquals("test", meta.getProperty(bean, "defaultStringVal"));
		Object[] args = new Object[]{meta.getProperty(bean, "defaultStringVal")};
		Consoles.log("$.defaultStringVal: {}", args);

	}
}

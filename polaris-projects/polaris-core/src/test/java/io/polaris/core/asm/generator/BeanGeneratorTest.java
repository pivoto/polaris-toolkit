package io.polaris.core.asm.generator;

import java.beans.PropertyDescriptor;

import io.polaris.core.asm.BaseAsmTest;
import io.polaris.core.asm.internal.AsmReflects;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeanGeneratorTest extends BaseAsmTest {

	@Test
	void test01() {
		for (int i = 0; i < 3; i++) {
			BeanGenerator bg = new BeanGenerator();
			bg.addProperty("sin", Double.TYPE);
			Class<?> c1 = bg.createClass();
			Class<?> c2 = bg.createClass();
			Object bean = bg.create();
			assertEquals(c1,c2);
			assertEquals(c1,bean.getClass());

			PropertyDescriptor[] pds = AsmReflects.getBeanProperties(bean.getClass());
			assertEquals(1, pds.length);
			assertEquals("sin", pds[0].getName());
			assertEquals(pds[0].getPropertyType(), Double.TYPE);
		}
	}
}

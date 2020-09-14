package io.polaris.core.lang.bean;

import io.polaris.core.asm.AsmPrint;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BeanMetadatasTest {


	@Test
	void test01() throws IOException {
		AsmPrint.print(BeanMetadataTest.class.getName());
	}

	@Test
	void test02() throws InstantiationException, IllegalAccessException, IOException {
		Class<BeanMetadata> c = BeanMetadataBuilder.buildMetadataClass(Bean01.class);
		BeanMetadata metadata = c.newInstance();
		System.out.println(metadata.types());
		System.out.println(metadata.getters());
		System.out.println(metadata.setters());
		AsmPrint.print(c, false);
	}
	@Test
	void test03() throws InstantiationException, IllegalAccessException {
		Class<BeanMetadata> c = BeanMetadataBuilder.buildMetadataClassWithInnerTypeRef(Bean01.class);
		BeanMetadata metadata = c.newInstance();
		System.out.println(metadata.types());
		System.out.println(metadata.getters());
		System.out.println(metadata.setters());
	}
}

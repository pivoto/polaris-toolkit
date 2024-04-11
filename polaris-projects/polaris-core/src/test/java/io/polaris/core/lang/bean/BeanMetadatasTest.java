package io.polaris.core.lang.bean;

import java.io.IOException;

import io.polaris.core.TestConsole;
import io.polaris.core.asm.AsmPrint;
import org.junit.jupiter.api.Test;

class BeanMetadatasTest {
	static {
		System.setProperty("java.memory.bytecode.tmpdir", "/data/classes");
	}


	@Test
	void test01() throws IOException {
		AsmPrint.print(BeanMetadataImpl.class.getName());
	}

	@Test
	void test02() throws InstantiationException, IllegalAccessException, IOException {
		Class<BeanMetadata> c = BeanMetadataBuilder.buildMetadataClass(Bean01.class);
		BeanMetadata metadata = c.newInstance();
		TestConsole.println(metadata.types());
		TestConsole.println(metadata.getters());
		TestConsole.println(metadata.setters());
		AsmPrint.print(c, false);
	}

//	@Test
//	void test03() throws InstantiationException, IllegalAccessException, IOException {
//		Class<BeanMetadata> c = BeanMetadataBuilder.buildMetadataClassWithInnerTypeRef(Bean01.class);
//		BeanMetadata metadata = c.newInstance();
//		TestConsole.println(metadata.types());
//		TestConsole.println(metadata.getters());
//		TestConsole.println(metadata.setters());
//		AsmPrint.print(c, false);
//	}
}

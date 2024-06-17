package io.polaris.core.lang.bean;

import java.io.IOException;

import io.polaris.core.TestConsole;
import io.polaris.core.asm.BaseAsmTest;
import io.polaris.core.asm.internal.AsmPrint;
import org.junit.jupiter.api.Test;

class BeanMetadatasV1Test extends BaseAsmTest {

	@Test
	void test01() throws IOException {
		AsmPrint.print(BeanMetadataV1Impl.class.getName());
	}

	@Test
	void test02() throws InstantiationException, IllegalAccessException, IOException {
		Class<BeanMetadataV1> c = BeanMetadataV1Builder.buildMetadataClass(Bean01.class);
		BeanMetadataV1 metadata = c.newInstance();
		TestConsole.println(metadata.types());
		TestConsole.println(metadata.getters());
		TestConsole.println(metadata.setters());
		AsmPrint.print(c, false);
	}

//	@Test
//	void test03() throws InstantiationException, IllegalAccessException, IOException {
//		Class<BeanMetadata> c = BeanMetadataV1Builder.buildMetadataClassWithInnerTypeRef(Bean01.class);
//		BeanMetadata metadata = c.newInstance();
//		TestConsole.println(metadata.types());
//		TestConsole.println(metadata.getters());
//		TestConsole.println(metadata.setters());
//		AsmPrint.print(c, false);
//	}
}

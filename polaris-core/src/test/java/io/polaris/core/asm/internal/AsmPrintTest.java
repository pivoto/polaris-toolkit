package io.polaris.core.asm.internal;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsmPrintTest {


	@Test
	void test01() throws IOException {

		AsmPrint.print(AsmPrint.class);

		AsmPrint.print(AsmPrint.class, false);
	}
}

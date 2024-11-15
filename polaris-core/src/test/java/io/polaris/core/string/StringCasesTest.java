package io.polaris.core.string;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class StringCasesTest {

	@Test
	void testToCamelCase() {
		String msg5 = StringCases.toCamelCase("java-name-case--xxx", '-');
		Consoles.println(msg5);
		String msg4 = StringCases.toDelimiterCase("java-name-case--xxx", '-');
		Consoles.println(msg4);
		String msg3 = StringCases.toDelimiterCase("javaNameCaseXxx", '-');
		Consoles.println(msg3);
		String msg2 = StringCases.toDelimiterCase("JavaNameCaseXxx", '-');
		Consoles.println(msg2);
		String msg1 = StringCases.toDelimiterCase("TTJavaNameCaseXxx", '-');
		Consoles.println(msg1);
		String msg = StringCases.toDelimiterCase("--TT-JavaNameCa-seXxx", '-');
		Consoles.println(msg);
	}
}

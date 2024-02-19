package io.polaris.core.string;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Test;

class StringCasesTest {

	@Test
	void testToCamelCase() {
		TestConsole.println(StringCases.toCamelCase("java-name-case--xxx", '-'));
		TestConsole.println(StringCases.toDelimiterCase("java-name-case--xxx", '-'));
		TestConsole.println(StringCases.toDelimiterCase("javaNameCaseXxx", '-'));
		TestConsole.println(StringCases.toDelimiterCase("JavaNameCaseXxx", '-'));
		TestConsole.println(StringCases.toDelimiterCase("TTJavaNameCaseXxx", '-'));
		TestConsole.println(StringCases.toDelimiterCase("--TT-JavaNameCa-seXxx", '-'));
	}
}

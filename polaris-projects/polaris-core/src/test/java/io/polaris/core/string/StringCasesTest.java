package io.polaris.core.string;

import org.junit.jupiter.api.Test;

class StringCasesTest {

	@Test
	void testToCamelCase() {
		System.out.println(StringCases.toCamelCase("java-name-case--xxx", '-'));
		System.out.println(StringCases.toDelimiterCase("java-name-case--xxx", '-'));
		System.out.println(StringCases.toDelimiterCase("javaNameCaseXxx", '-'));
		System.out.println(StringCases.toDelimiterCase("JavaNameCaseXxx", '-'));
		System.out.println(StringCases.toDelimiterCase("TTJavaNameCaseXxx", '-'));
		System.out.println(StringCases.toDelimiterCase("--TT-JavaNameCa-seXxx", '-'));
	}
}

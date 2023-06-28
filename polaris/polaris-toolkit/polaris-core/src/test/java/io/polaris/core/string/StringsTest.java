package io.polaris.core.string;

import org.junit.jupiter.api.Test;

class StringsTest {

	@Test
	void test01() {
		System.out.printf("[%s]%n", Strings.trimStart("    abc   "));
		System.out.printf("[%s]%n", Strings.trimEnd("    abc   "));
	}

	@Test
	void test02() {
		System.out.printf("[%s]%n", Strings.trimStart("xxxabcxxx", 'x'));
		System.out.printf("[%s]%n", Strings.trimEnd("xxxabcxxx", 'x'));
	}
}

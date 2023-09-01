package io.polaris.core.string;

import io.polaris.core.junit.Fast;
import io.polaris.core.reflect.Reflects;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class StringsTest {

	@Test
	@Fast
	void test01() {
		System.out.printf("[%s]%n", Strings.trimStart("    abc   "));
		System.out.printf("[%s]%n", Strings.trimEnd("    abc   "));

		System.out.println(Reflects.getMethodByName(StringsTest.class,"test01").getAnnotationsByType(Tag.class));
	}

	@Test
	void test02() {
		System.out.printf("[%s]%n", Strings.trimStart("xxxabcxxx", 'x'));
		System.out.printf("[%s]%n", Strings.trimEnd("xxxabcxxx", 'x'));
	}
}

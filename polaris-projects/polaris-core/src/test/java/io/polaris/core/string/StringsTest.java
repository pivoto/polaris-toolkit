package io.polaris.core.string;

import io.polaris.core.junit.Fast;
import io.polaris.core.reflect.Reflects;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class StringsTest {

	@Test
	@Fast
	void test01() {
		System.out.printf("[%s]%n", Strings.trimStart("    abc   "));
		System.out.printf("[%s]%n", Strings.trimEnd("    abc   "));

		System.out.println(Reflects.getMethodByName(StringsTest.class, "test01").getAnnotationsByType(Tag.class));
	}

	@Test
	void test02() {
		System.out.printf("[%s]%n", Strings.trimStart("xxxabcxxx", 'x'));
		System.out.printf("[%s]%n", Strings.trimEnd("xxxabcxxx", 'x'));
	}


	@Test
	void test03() {
		Map<String, String> map = new HashMap<>();
		map.put("ctx.aaa", "aaa123");
		map.put("ctx.bbb", "bbb123");
		System.out.println(Strings.resolvePlaceholders("aaa ${ctx.aaa:/}", map::get));
		System.out.println(Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.bbb:/}}", map::get));
		System.out.println(Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:/}}", map::get));
	}

	@Test
	void test04() {
		System.out.println(Arrays.toString(Strings.tokenizeToArray("aaa,bbb,ccc", ",")));
		System.out.println(Arrays.toString(Strings.tokenizeToArray("aaa,;bbb,ccc", ",;")));
		System.out.println(Arrays.toString(Strings.delimitedToArray("aaa,;bbb,ccc", ",;")));
	}

	@Test
	void test05() {
		System.out.println(Arrays.toString(Strings.tokenizeToArray("aaa|bbb||ccc", "|")));
		System.out.println(Arrays.toString(Strings.tokenizeToArray("aaa,|bbb||ccc", ",|")));
		System.out.println(Arrays.toString(Strings.delimitedToArray("aaa,|bbb||ccc", ",|")));
	}
}

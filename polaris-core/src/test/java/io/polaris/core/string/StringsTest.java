package io.polaris.core.string;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.polaris.core.TestConsole;
import io.polaris.core.junit.Fast;
import io.polaris.core.lang.annotation.Annotations;
import io.polaris.core.reflect.Reflects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class StringsTest {

	@Test
	@Fast
	void test01() {
		Assertions.assertEquals(Strings.trimStart("    abc   "), "abc   ");
		Assertions.assertEquals(Strings.trimEnd("    abc   "), "    abc");
		Assertions.assertEquals(Strings.trim("    abc   "), "abc");

		String methodName = Reflects.getPropertyName(StringsTest::test01);
		TestConsole.println("methodName: {}", methodName);
		Method method = Reflects.getMethodByName(getClass(), methodName);
		TestConsole.println(Arrays.toString(method.getAnnotationsByType(Tag.class)));
		TestConsole.println(Arrays.toString(Annotations.getRepeatableAnnotation(method, Tag.class)));
		TestConsole.println(Annotations.getMergedRepeatableAnnotation(method, Tag.class));
		;
	}

	@Test
	void test02() {
		TestConsole.println(Strings.trimStart("xxxabcxxx", 'x'));
		TestConsole.println(Strings.trimEnd("xxxabcxxx", 'x'));
		Assertions.assertEquals(Strings.trimStart("xxxabcxxx", 'x'), "abcxxx");
		Assertions.assertEquals(Strings.trimEnd("xxxabcxxx", 'x'), "xxxabc");
		Assertions.assertEquals(Strings.trim("xxxabcxxx", 'x'), "abc");
	}


	@Test
	void test03() {
		Map<String, String> map = new HashMap<>();
		map.put("ctx.aaa", "aaa123");
		map.put("ctx.bbb", "bbb123");
		TestConsole.println(Strings.resolvePlaceholders("aaa ${ctx.aaa:/}", map::get));
		TestConsole.println(Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.bbb:/}}", map::get));
		TestConsole.println(Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:/}}", map::get));
		Assertions.assertEquals("aaa aaa123", Strings.resolvePlaceholders("aaa ${ctx.aaa:/}", map::get));
		Assertions.assertEquals("aaa bbb123", Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.bbb:/}}", map::get));
		Assertions.assertEquals("aaa /", Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:/}}", map::get));
	}

	@Test
	void test04() {
		TestConsole.println(Arrays.toString(Strings.tokenizeToArray("aaa,bbb,ccc", ",")));
		TestConsole.println(Arrays.toString(Strings.tokenizeToArray("aaa,;bbb,ccc", ",;")));
		TestConsole.println(Arrays.toString(Strings.delimitedToArray("aaa,;bbb,ccc", ",;")));
		Assertions.assertEquals(3, Strings.tokenizeToArray("aaa,bbb,ccc", ",").length);
		Assertions.assertEquals(3, Strings.tokenizeToArray("aaa,;bbb,ccc", ",;").length);
		Assertions.assertEquals(2, Strings.delimitedToArray("aaa,;bbb,ccc", ",;").length);
	}

	@Test
	void test05() {
		TestConsole.println(Arrays.toString(Strings.tokenizeToArray("aaa|bbb||ccc", "|")));
		TestConsole.println(Arrays.toString(Strings.tokenizeToArray("aaa,|bbb||ccc", ",|")));
		TestConsole.println(Arrays.toString(Strings.delimitedToArray("aaa,|bbb||ccc", ",|")));
		Assertions.assertEquals(3, Strings.tokenizeToArray("aaa|bbb||ccc", "|").length);
		Assertions.assertEquals(3, Strings.tokenizeToArray("aaa,|bbb||ccc", ",|").length);
		Assertions.assertEquals(2, Strings.delimitedToArray("aaa,|bbb||ccc", ",|").length);
	}

	@Test
	void test06() {
		Map<String, String> map = new HashMap<>();
		map.put("ctx.aaa", "aaa123");
		map.put("ctx.bbb", "bbb123");
		TestConsole.println(Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:${eee}}}", map::get));
		TestConsole.println(Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:${eee:-xx}}}", map::get));
		TestConsole.println(Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:${eee:xx}}}", map::get));
		map.put("eee", "");
		TestConsole.println(Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:${eee:-xx}}}", map::get));
		TestConsole.println(Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:${eee:xx}}}", map::get));
	}

}

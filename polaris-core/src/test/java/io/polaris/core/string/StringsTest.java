package io.polaris.core.string;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.polaris.core.io.Consoles;
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
		Consoles.println("methodName: {}", methodName);
		Method method = Reflects.getMethodByName(getClass(), methodName);
		String msg1 = Arrays.toString(method.getAnnotationsByType(Tag.class));
		Consoles.println(msg1);
		String msg = Arrays.toString(Annotations.getRepeatableAnnotation(method, Tag.class));
		Consoles.println(msg);
		Object[] args = new Object[]{Annotations.getMergedRepeatableAnnotation(method, Tag.class)};
		Consoles.println(args);
		;
	}

	@Test
	void test02() {
		String msg1 = Strings.trimStart("xxxabcxxx", 'x');
		Consoles.println(msg1);
		String msg = Strings.trimEnd("xxxabcxxx", 'x');
		Consoles.println(msg);
		Assertions.assertEquals(Strings.trimStart("xxxabcxxx", 'x'), "abcxxx");
		Assertions.assertEquals(Strings.trimEnd("xxxabcxxx", 'x'), "xxxabc");
		Assertions.assertEquals(Strings.trim("xxxabcxxx", 'x'), "abc");
	}


	@Test
	void test03() {
		Map<String, String> map = new HashMap<>();
		map.put("ctx.aaa", "aaa123");
		map.put("ctx.bbb", "bbb123");
		String msg2 = Strings.resolvePlaceholders("aaa ${ctx.aaa:/}", map::get);
		Consoles.println(msg2);
		String msg1 = Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.bbb:/}}", map::get);
		Consoles.println(msg1);
		String msg = Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:/}}", map::get);
		Consoles.println(msg);
		Assertions.assertEquals("aaa aaa123", Strings.resolvePlaceholders("aaa ${ctx.aaa:/}", map::get));
		Assertions.assertEquals("aaa bbb123", Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.bbb:/}}", map::get));
		Assertions.assertEquals("aaa /", Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:/}}", map::get));
	}

	@Test
	void test04() {
		String msg2 = Arrays.toString(Strings.tokenizeToArray("aaa,bbb,ccc", ","));
		Consoles.println(msg2);
		String msg1 = Arrays.toString(Strings.tokenizeToArray("aaa,;bbb,ccc", ",;"));
		Consoles.println(msg1);
		String msg = Arrays.toString(Strings.delimitedToArray("aaa,;bbb,ccc", ",;"));
		Consoles.println(msg);
		Assertions.assertEquals(3, Strings.tokenizeToArray("aaa,bbb,ccc", ",").length);
		Assertions.assertEquals(3, Strings.tokenizeToArray("aaa,;bbb,ccc", ",;").length);
		Assertions.assertEquals(2, Strings.delimitedToArray("aaa,;bbb,ccc", ",;").length);
	}

	@Test
	void test05() {
		String msg2 = Arrays.toString(Strings.tokenizeToArray("aaa|bbb||ccc", "|"));
		Consoles.println(msg2);
		String msg1 = Arrays.toString(Strings.tokenizeToArray("aaa,|bbb||ccc", ",|"));
		Consoles.println(msg1);
		String msg = Arrays.toString(Strings.delimitedToArray("aaa,|bbb||ccc", ",|"));
		Consoles.println(msg);
		Assertions.assertEquals(3, Strings.tokenizeToArray("aaa|bbb||ccc", "|").length);
		Assertions.assertEquals(3, Strings.tokenizeToArray("aaa,|bbb||ccc", ",|").length);
		Assertions.assertEquals(2, Strings.delimitedToArray("aaa,|bbb||ccc", ",|").length);
	}

	@Test
	void test06() {
		Map<String, String> map = new HashMap<>();
		map.put("ctx.aaa", "aaa123");
		map.put("ctx.bbb", "bbb123");
		String msg4 = Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:${eee}}}", map::get);
		Consoles.println(msg4);
		String msg3 = Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:${eee:-xx}}}", map::get);
		Consoles.println(msg3);
		String msg2 = Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:${eee:xx}}}", map::get);
		Consoles.println(msg2);
		map.put("eee", "");
		String msg1 = Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:${eee:-xx}}}", map::get);
		Consoles.println(msg1);
		String msg = Strings.resolvePlaceholders("aaa ${ctx.ccc:${ctx.ddd:${eee:xx}}}", map::get);
		Consoles.println(msg);
	}

	@Test
	void test07() {
		Consoles.println(Strings.normalize("app"));
		Consoles.println(Strings.normalize("app-name"));
		Consoles.println(Strings.normalize("$app-name"));
		Consoles.println(Strings.normalize("$app name"));
	}

	@Test
	void test08() {
		Consoles.println(Strings.getExistedSystemProperty("platform.dir", "user.dir"));
	}
}

package io.polaris.core.javapoet;

import joptsimple.internal.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Feb 28, 2026
 */
public class TestCodeBlock {
	@Test
	void test01() {

		Assertions.assertEquals("literal", CodeBlock.of("$N", "literal").toString());
		Assertions.assertEquals("org.test.Test", CodeBlock.of("$N", TypeSpec.classBuilder("org.test.Test").build()).toString());

		Assertions.assertEquals("literal", CodeBlock.of("$L", "literal").toString());
		Assertions.assertEquals("int", CodeBlock.of("$L", int.class).toString());
		Assertions.assertEquals("class java.lang.Integer", CodeBlock.of("$L", Integer.class).toString());
		Assertions.assertEquals("java.lang.String", CodeBlock.of("$L", TypeName.get(String.class)).toString());
		Assertions.assertEquals(TypeSpec.classBuilder("Test").build().toString(), CodeBlock.of("$L", TypeSpec.classBuilder("Test").build().toString()).toString());

		Assertions.assertEquals("java.lang.String", CodeBlock.of("$T", String.class).toString());
		Assertions.assertEquals("//增加缩进级别\n" +
			"\t//增加缩进级别", CodeBlock.of("$>//增加缩进级别\n//增加缩进级别").toString());
		Assertions.assertEquals("//增加缩进级别\n" +
			"\t//增加缩进级别\n" +
			"//减少缩进级别", CodeBlock.of("$>//增加缩进级别\n//增加缩进级别\n$<//减少缩进级别").toString());


		Assertions.assertEquals("$", CodeBlock.of("$$", String.class).toString());
		Assertions.assertEquals("\"literal\"", CodeBlock.of("$S", "literal").toString());
		Assertions.assertEquals("\"int\"", CodeBlock.of("$S", int.class).toString());
		Assertions.assertEquals("\"class java.lang.Integer\"", CodeBlock.of("$S", Integer.class).toString());
		Assertions.assertEquals("\"class java.lang.String\"", CodeBlock.of("$S", String.class).toString());
		Assertions.assertEquals("\"java.lang.String\"", CodeBlock.of("$S", TypeName.get(String.class)).toString());

		Assertions.assertEquals("if(true) {\n" +
				"\t//literal\n" +
				"}\n",
			CodeBlock.builder()
				.beginControlFlow("if(true)")
				.add("//$L\n", "literal")
				.endControlFlow()
				.build().toString());

		Assertions.assertEquals("//开始一个语句。对于多行语句，第一行之后的每一行都会双倍缩进。\n" +
			"\t\tliteral\n" +
			"//结束一个语句\n" +
			"//string", CodeBlock.builder()
			.add("$[//开始一个语句。对于多行语句，第一行之后的每一行都会双倍缩进。\n", "string")
			.add("$L\n", "literal")
			.add("$]//结束一个语句\n//$L", "string")
			.build().toString());

		Assertions.assertEquals(repeat("Tests", 20).trim()
				+ "\n\t\t" + repeat("Tests", 10).trim(),
			CodeBlock.of(repeat("Tests$Z", 30)).toString());
		Assertions.assertEquals(repeat("Test ", 20).trim()
				+ "\n\t\t" + repeat("Test ", 10).trim(),
			CodeBlock.of(repeat("Test$W", 30)).toString());
	}

	String repeat(String str, int count) {

		final int len = str.length();
		final long longSize = (long) len * (long) count;
		final int size = (int) longSize;
		if (size != longSize) {
			throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);
		}

		final char[] array = new char[size];
		str.getChars(0, len, array, 0);
		int n;
		for (n = len; n < size - n; n <<= 1) {
			System.arraycopy(array, 0, array, n, n);
		}
		System.arraycopy(array, 0, array, n, size - n);
		return new String(array);
	}
}
